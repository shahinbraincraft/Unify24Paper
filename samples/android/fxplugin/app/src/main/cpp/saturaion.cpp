//================================================================================
//
// (c) Copyright China Digital Video (Beijing) Limited, 2020. All rights reserved.
//
// This code and information is provided "as is" without warranty of any kind,
// either expressed or implied, including but not limited to the implied
// warranties of merchantability and/or fitness for a particular purpose.
//
//--------------------------------------------------------------------------------
//   Birth Date:    Aug 26. 2020
//   Author:        Meishe video team
//================================================================================
#include <fxplugin/MfxpCore.h>
#include <fxplugin/MfxpVideoFx.h>
#include "glutils.h"

#include <memory>
#include <math.h>


#define SATURATION_LEVEL_PARAM_NAME         "Saturation Level"

static MfxpHost *g_pluginHost = nullptr;
static MfxpPropertySuite *g_propSuite = nullptr;
static MfxpParameterSuite *g_paramSuite = nullptr;
static MfxpVideoEffectSuite *g_videoEffectSuite = nullptr;


// Function to set the host structure
static void PluginSetHost(MfxpHost *host)
{
    g_pluginHost = host;
}

static EMfxpStatus OnLoadPlugin()
{
    // Fetch the host suites out of the global host pointer
    if (g_pluginHost == nullptr)
        return keMfxpStatErrMissingHostFeature;

    g_propSuite = reinterpret_cast<MfxpPropertySuite *>(g_pluginHost->fetchSuite(g_pluginHost->host, keMfxpSuiteType_Property));
    g_paramSuite = reinterpret_cast<MfxpParameterSuite *>(g_pluginHost->fetchSuite(g_pluginHost->host, keMfxpSuiteType_Param));
    g_videoEffectSuite = reinterpret_cast<MfxpVideoEffectSuite *>(g_pluginHost->fetchSuite(g_pluginHost->host, keMfxpSuiteType_VideoEffect));
    if (g_propSuite == nullptr || g_paramSuite == nullptr || g_videoEffectSuite == nullptr)
        return keMfxpStatErrMissingHostFeature;

    return keMfxpStatOK;
}

static EMfxpStatus OnUnloadPlugin()
{
    // Nothing to do here
    return keMfxpStatOK;
}

static EMfxpStatus OnDescribePlugin(MfxpVideoEffectHandle effectDesc)
{
    if (effectDesc == nullptr)
        return keMfxpStatErrBadHandle;

    EMfxpStatus status;

    // Get the property handle for the plugin(video effect descriptor)
    MfxpPropertySetHandle effectDescProp;
    status = g_videoEffectSuite->getPropertySet(effectDesc, &effectDescProp);
    if (status != keMfxpStatOK)
        return status;

    // Tell plugin host we are a filter
    g_propSuite->propSetInt(effectDescProp, keMfxpPropVideoEffectType, 0, keMfxpVideoEffectType_Filter);

    // Tell plugin host we can only render in OpenGL context
    const int renderContext = keMfxpVideoEffectRenderContext_OpenGL;
    g_propSuite->propSetIntN(effectDescProp, keMfxpPropVideoEffectSupportedVideoEffectRenderContexts, 1, &renderContext);

    // Tell plugin host we can handle texture Y direction of any form
    g_propSuite->propSetInt(effectDescProp, keMfxpPropVideoEffectInputOpenGLTextureYDir, 0, keMfxpOpenGLTextureYDir_Any);

    g_propSuite->propSetString(effectDescProp, keMfxpPropLabel, 0, "Saturation (HSL)");
    g_propSuite->propSetString(effectDescProp, keMfxpPropPluginDescription, 0, "Adjust saturation with HSL color model");

    //
    // Define paramters (if there is any)
    //
    MfxpParamSetHandle paramSetDesc;
    status = g_videoEffectSuite->getParamSet(effectDesc, &paramSetDesc);
    if (status != keMfxpStatOK)
        return status;

    MfxpPropertySetHandle paramDescProp;
    status = g_paramSuite->paramDefine(paramSetDesc, SATURATION_LEVEL_PARAM_NAME, keMfxpParamType_Double, &paramDescProp);
    if (status != keMfxpStatOK)
        return status;

    g_propSuite->propSetDouble(paramDescProp, keMfxpPropParamDefault, 0, 1);
    g_propSuite->propSetDouble(paramDescProp, keMfxpPropParamMin, 0, 0);
    g_propSuite->propSetDouble(paramDescProp, keMfxpPropParamMax, 0, 2);
    g_propSuite->propSetString(paramDescProp, keMfxpPropParamTip, 0, "Saturation Level");

    //
    // Define pin
    //
    MfxpPropertySetHandle pinDescProp;

    // Define the output pin
    status = g_videoEffectSuite->pinDefine(effectDesc, MfxpVideoEffectOutputPinName, false, &pinDescProp);
    if (status != keMfxpStatOK)
        return status;

    // Define the input pin
    status = g_videoEffectSuite->pinDefine(effectDesc, MfxpVideoEffectSimpleSourcePinName, true, &pinDescProp);
    if (status != keMfxpStatOK)
        return status;

    return keMfxpStatOK;
}

struct PluginInstanceData {
    GLuint shaderProgram = 0;
    GLint attrLoc_pos;
    GLint attrLoc_texCoord;
    GLint uniformLoc_saturation;
};

static EMfxpStatus OnCreateInstance(MfxpVideoEffectHandle effectInstance)
{
    if (effectInstance == nullptr)
        return keMfxpStatErrBadHandle;

    EMfxpStatus status;

    // Get the property handle for the plugin instance(video effect instance)
    MfxpPropertySetHandle effectInstanceProp;
    status = g_videoEffectSuite->getPropertySet(effectInstance, &effectInstanceProp);
    if (status != keMfxpStatOK)
        return status;

    PluginInstanceData *instanceData = new PluginInstanceData;
    // NOTE: We can't initilaize GPU related resources here
    g_propSuite->propSetPointer(effectInstanceProp, keMfxpPropInstanceData, 0, instanceData);

    return keMfxpStatOK;
}

static PluginInstanceData * FetchInstanceDataFromVideoEffectHandle(MfxpVideoEffectHandle effectInstance)
{
    if (effectInstance == nullptr)
        return nullptr;

    // Get the property handle for the plugin instance(video effect instance)
    MfxpPropertySetHandle effectInstanceProp;
    const EMfxpStatus status = g_videoEffectSuite->getPropertySet(effectInstance, &effectInstanceProp);
    if (status != keMfxpStatOK)
        return nullptr;

    void *ptrVal = nullptr;
    g_propSuite->propGetPointer(effectInstanceProp, keMfxpPropInstanceData, 0, &ptrVal);
    return reinterpret_cast<PluginInstanceData *>(ptrVal);
}

static EMfxpStatus OnDestroyInstance(MfxpVideoEffectHandle effectInstance)
{
    PluginInstanceData *instanceData = FetchInstanceDataFromVideoEffectHandle(effectInstance);
    if (instanceData == nullptr)
        return keMfxpStatErrBadHandle;

    delete instanceData;
    return keMfxpStatOK;
}

static const char *g_vertShaderSource =
        "attribute highp vec2 posAttr;\n"
        "attribute highp vec2 texCoordAttr;\n"
        "varying highp vec2 texCoord;\n"
        "void main()\n"
        "{\n"
        "    texCoord = texCoordAttr;\n"
        "    gl_Position = vec4(posAttr, 0, 1);\n"
        "}\n";

static const char *g_fragShaderSource =
        "uniform sampler2D sampler;\n"
        "uniform lowp float saturation;\n"
        "varying highp vec2 texCoord;\n"
        "void main()\n"
        "{\n"
        "    lowp vec4 color = texture2D(sampler, texCoord);\n"
        "    lowp float minRGB = min(color.r, min(color.g, color.b));\n"
        "    lowp float maxRGB = max(color.r, max(color.g, color.b));\n"
        "    lowp vec3 lightness = vec3((minRGB + maxRGB) / 2.0);\n"
        "    gl_FragColor = vec4(mix(lightness, color.rgb, saturation), color.a);\n"
        "}\n";

static EMfxpStatus OnInitInstance(MfxpVideoEffectHandle effectInstance)
{
    PluginInstanceData *instanceData = FetchInstanceDataFromVideoEffectHandle(effectInstance);
    if (instanceData == nullptr)
        return keMfxpStatErrBadHandle;

    // Now plugin host has setup OpenGL context for us
    // It is safe to initialize OpenGL related resources here
    // Build shader program
    instanceData->shaderProgram = CreateShaderProgram(
                g_vertShaderSource,
                g_fragShaderSource);
    if (!instanceData->shaderProgram)
        return keMfxpStatErrFailed;

    instanceData->attrLoc_pos = glGetAttribLocation(instanceData->shaderProgram, "posAttr");
    instanceData->attrLoc_texCoord = glGetAttribLocation(instanceData->shaderProgram, "texCoordAttr");
    instanceData->uniformLoc_saturation = glGetUniformLocation(instanceData->shaderProgram, "saturation");
    return keMfxpStatOK;
}

static EMfxpStatus OnCleanupInstance(MfxpVideoEffectHandle effectInstance)
{
    PluginInstanceData *instanceData = FetchInstanceDataFromVideoEffectHandle(effectInstance);
    if (instanceData == nullptr)
        return keMfxpStatErrBadHandle;

    glDeleteProgram(instanceData->shaderProgram);
    return keMfxpStatOK;
}

static EMfxpStatus OnIsIdentity(
        MfxpVideoEffectHandle effectInstance,
        MfxpPropertySetHandle /* inArgs */,
        MfxpPropertySetHandle outArgs)
{
    if (effectInstance == nullptr)
        return keMfxpStatErrBadHandle;

    EMfxpStatus status;

    MfxpParamSetHandle paramSetInstance;
    status = g_videoEffectSuite->getParamSet(effectInstance, &paramSetInstance);
    if (status != keMfxpStatOK)
        return status;

    MfxpParamHandle paramInstance;
    status = g_paramSuite->paramGetHandle(paramSetInstance, SATURATION_LEVEL_PARAM_NAME, &paramInstance, nullptr);
    if (status != keMfxpStatOK)
        return status;

    double level = 1;
    g_paramSuite->paramGetValue(paramInstance, &level);
    if (fabs(level - 1) < 0.001)
        g_propSuite->propSetString(outArgs, keMfxpPropName, 0, MfxpVideoEffectSimpleSourcePinName);

    return keMfxpStatOK;
}

static EMfxpStatus RenderEffect(
        MfxpVideoEffectHandle effectInstance,
        PluginInstanceData *instanceData,
        MfxpPropertySetHandle inputVideoFrame,
        MfxpPropertySetHandle outputVideoFrame)
{
    EMfxpStatus status;

    int inputTexId = 0;
    g_propSuite->propGetInt(inputVideoFrame, keMfxpPropVideoFrameOpenGLTexId, 0, &inputTexId);
    int inputTexIsUpsideDown = 0;
    g_propSuite->propGetInt(inputVideoFrame, keMfxpPropVideoFrameOpenGLUpsideDownTexture, 0, &inputTexIsUpsideDown);

    int outputTexId = 0;
    g_propSuite->propGetInt(outputVideoFrame, keMfxpPropVideoFrameOpenGLTexId, 0, &outputTexId);
    int outputTexTarget = GL_TEXTURE_2D;
    g_propSuite->propGetInt(outputVideoFrame, keMfxpPropVideoFrameOpenGLTexTarget, 0, &outputTexTarget);

    //
    // Get effect parameter(s)
    //
    MfxpParamSetHandle paramSetInstance;
    status = g_videoEffectSuite->getParamSet(effectInstance, &paramSetInstance);
    if (status != keMfxpStatOK)
        return status;

    MfxpParamHandle paramInstance;
    status = g_paramSuite->paramGetHandle(paramSetInstance, SATURATION_LEVEL_PARAM_NAME, &paramInstance, nullptr);
    if (status != keMfxpStatOK)
        return status;

    double level = 1;
    g_paramSuite->paramGetValue(paramInstance, &level);

    glUseProgram(instanceData->shaderProgram);
    glUniform1f(instanceData->uniformLoc_saturation, GLfloat(level));

    //
    // Setup viewport
    //
    int videoFrameBounds[4];
    status = g_propSuite->propGetIntN(outputVideoFrame, keMfxpPropVideoFrameBounds, 4, videoFrameBounds);
    if (status != keMfxpStatOK)
        return status;

    const int widthInPixel = videoFrameBounds[2] - videoFrameBounds[0];
    const int heightInPixel = videoFrameBounds[3] - videoFrameBounds[1];
    glViewport(0, 0, GLsizei(widthInPixel), GLsizei(heightInPixel));

    // Setup frame buffer
    glBindTexture(outputTexTarget, GLuint(outputTexId));
    glTexParameteri(outputTexTarget, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
    glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, outputTexTarget, GLuint(outputTexId), 0);

    glBindTexture(GL_TEXTURE_2D, GLuint(inputTexId));
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

    struct VertexData {
        GLfloat x, y;
        GLfloat s, t;
    } vertices[4];

    vertices[0].x = -1;
    vertices[0].y = 1;
    vertices[0].s = 0;
    vertices[0].t = GLfloat(inputTexIsUpsideDown ? 0 : 1);

    vertices[1].x = -1;
    vertices[1].y = -1;
    vertices[1].s = 0;
    vertices[1].t = GLfloat(inputTexIsUpsideDown ? 1 : 0);

    vertices[2].x = 1;
    vertices[2].y = 1;
    vertices[2].s = 1;
    vertices[2].t = GLfloat(inputTexIsUpsideDown ? 0 : 1);

    vertices[3].x = 1;
    vertices[3].y = -1;
    vertices[3].s = 1;
    vertices[3].t = GLfloat(inputTexIsUpsideDown ? 1 : 0);

    glVertexAttribPointer(
                instanceData->attrLoc_pos, 2, GL_FLOAT, GL_FALSE,
                GLsizei(sizeof(VertexData)),
                reinterpret_cast<const GLvoid *>(&vertices[0].x));
    glVertexAttribPointer(
                instanceData->attrLoc_texCoord, 2, GL_FLOAT, GL_FALSE,
                GLsizei(sizeof(VertexData)),
                reinterpret_cast<const GLvoid *>(&vertices[0].s));

    glEnableVertexAttribArray(instanceData->attrLoc_pos);
    glEnableVertexAttribArray(instanceData->attrLoc_texCoord);

    glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);

    glDisableVertexAttribArray(instanceData->attrLoc_pos);
    glDisableVertexAttribArray(instanceData->attrLoc_texCoord);

    glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, outputTexTarget, 0, 0);

    // Tell plugin host the output texture we have rendered is NOT upside down
    g_propSuite->propSetInt(outputVideoFrame, keMfxpPropVideoFrameOpenGLUpsideDownTexture, 0, 0);

    return keMfxpStatOK;
}

static EMfxpStatus OnRender(MfxpVideoEffectHandle effectInstance)
{
    PluginInstanceData *instanceData = FetchInstanceDataFromVideoEffectHandle(effectInstance);
    if (instanceData == nullptr)
        return keMfxpStatErrBadHandle;

    EMfxpStatus status;

    //
    // Get input video frame
    //
    MfxpVideoEffectPinHandle inputPinInstance;
    status = g_videoEffectSuite->pinGetHandle(effectInstance, MfxpVideoEffectSimpleSourcePinName, &inputPinInstance, nullptr);
    if (status != keMfxpStatOK)
        return status;

    MfxpPropertySetHandle inputVideoFrame;
    status = g_videoEffectSuite->pinGetVideoFrame(inputPinInstance, &inputVideoFrame);
    if (status != keMfxpStatOK)
        return status;

    //
    // Get output video frame
    //
    MfxpVideoEffectPinHandle outputPinInstance;
    status = g_videoEffectSuite->pinGetHandle(effectInstance, MfxpVideoEffectOutputPinName, &outputPinInstance, nullptr);
    if (status != keMfxpStatOK) {
        g_videoEffectSuite->pinReleaseVideoFrame(inputVideoFrame);
        return status;
    }

    MfxpPropertySetHandle outputVideoFrame;
    status = g_videoEffectSuite->pinGetVideoFrame(outputPinInstance, &outputVideoFrame);
    if (status != keMfxpStatOK) {
        g_videoEffectSuite->pinReleaseVideoFrame(inputVideoFrame);
        return status;
    }

    status = RenderEffect(effectInstance, instanceData, inputVideoFrame, outputVideoFrame);
    g_videoEffectSuite->pinReleaseVideoFrame(inputVideoFrame);
    g_videoEffectSuite->pinReleaseVideoFrame(outputVideoFrame);
    return status;
}

// The main entry point function
static EMfxpStatus PluginEntryPoint(
        EMfxpActionType actionType,
        const void *handle,
        MfxpPropertySetHandle inArgs,
        MfxpPropertySetHandle outArgs)
{
    switch (actionType) {
    case keMfxpActionType_LoadPlugin:
        return OnLoadPlugin();

    case keMfxpActionType_UnloadPlugin:
        return OnUnloadPlugin();

    case keMfxpActionType_DescribePlugin:
        return OnDescribePlugin(MfxpVideoEffectHandle(handle));

    case keMfxpActionType_CreateInstance:
        return OnCreateInstance(MfxpVideoEffectHandle(handle));

    case keMfxpActionType_DestroyInstance:
        return OnDestroyInstance(MfxpVideoEffectHandle(handle));

    case keMfxpActionType_InitInstance:
        return OnInitInstance(MfxpVideoEffectHandle(handle));

    case keMfxpActionType_CleanupInstance:
        return OnCleanupInstance(MfxpVideoEffectHandle(handle));

    case keMfxpActionType_ReleaseCachedResources:
        return keMfxpStatOK;

    case keMfxpActionType_IsIdentity:
        return OnIsIdentity(MfxpVideoEffectHandle(handle), inArgs, outArgs);

    case keMfxpActionType_Render:
        return OnRender(MfxpVideoEffectHandle(handle));

    default:
        return keMfxpStatOK;
    }
}

MfxpPlugin g_saturationPlugin =
{
    keMfxpApiType_VideoEffect,  // Indicate this is a video effect plugin
    "com.meishesdk.saturation", // Unique name to identify this plugin
    1,                          // Major version of this plugin
    0,                          // Minor version of this plugin
    &PluginSetHost,
    &PluginEntryPoint
};

