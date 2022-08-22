//================================================================================
//
// (c) Copyright China Digital Video (Beijing) Limited, 2020. All rights reserved.
//
// This code and information is provided "as is" without warranty of any kind,
// either expressed or implied, including but not limited to the implied
// warranties of merchantability and/or fitness for a particular purpose.
//
//--------------------------------------------------------------------------------
//   Birth Date:    Sep 02. 2020
//   Author:        Meishe video team
//================================================================================
#include <fxplugin/MfxpCore.h>
#include <fxplugin/MfxpVideoFx.h>
#include "glutils.h"

#include <memory>
#include <math.h>


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

    // Tell plugin host we are a transition
    g_propSuite->propSetInt(effectDescProp, keMfxpPropVideoEffectType, 0, keMfxpVideoEffectType_Transition);

    // Tell plugin host we can only render in OpenGL context
    const int renderContext = keMfxpVideoEffectRenderContext_OpenGL;
    g_propSuite->propSetIntN(effectDescProp, keMfxpPropVideoEffectSupportedVideoEffectRenderContexts, 1, &renderContext);

    // Tell plugin host we can handle texture Y direction of any form
    g_propSuite->propSetInt(effectDescProp, keMfxpPropVideoEffectInputOpenGLTextureYDir, 0, keMfxpOpenGLTextureYDir_Any);

    g_propSuite->propSetString(effectDescProp, keMfxpPropLabel, 0, "Circle In");
    g_propSuite->propSetString(effectDescProp, keMfxpPropPluginDescription, 0, "Transition in a circle in manner");

    //
    // Define paramters (if there is any)
    //
    MfxpParamSetHandle paramSetDesc;
    status = g_videoEffectSuite->getParamSet(effectDesc, &paramSetDesc);
    if (status != keMfxpStatOK)
        return status;

    MfxpPropertySetHandle paramDescProp;
    status = g_paramSuite->paramDefine(paramSetDesc, MfxpVideoEffectTransitionProgressParamName, keMfxpParamType_Double, &paramDescProp);
    if (status != keMfxpStatOK)
        return status;

    g_propSuite->propSetDouble(paramDescProp, keMfxpPropParamDefault, 0, 0);
    g_propSuite->propSetDouble(paramDescProp, keMfxpPropParamMin, 0, 0);
    g_propSuite->propSetDouble(paramDescProp, keMfxpPropParamMax, 0, 1);
    g_propSuite->propSetString(paramDescProp, keMfxpPropParamTip, 0, "Trasition Progress");

    //
    // Define pin
    //
    MfxpPropertySetHandle pinDescProp;

    // Define the output pin
    status = g_videoEffectSuite->pinDefine(effectDesc, MfxpVideoEffectOutputPinName, false, &pinDescProp);
    if (status != keMfxpStatOK)
        return status;

    // Define the transition source input pin
    status = g_videoEffectSuite->pinDefine(effectDesc, MfxpVideoEffectTransitionSourceFromClipName, true, &pinDescProp);
    if (status != keMfxpStatOK)
        return status;

    // Define the transition destination input pin
    status = g_videoEffectSuite->pinDefine(effectDesc, MfxpVideoEffectTransitionSourceToClipName, true, &pinDescProp);
    if (status != keMfxpStatOK)
        return status;

    return keMfxpStatOK;
}

struct PluginInstanceData {
    GLuint shaderProgram = 0;
    GLint attrLoc_pos;
    GLint attrLoc_srcTexCoord;
    GLint attrLoc_dstTexCoord;
    GLint uniformLoc_progress;
    GLint uniformLoc_imgAspectRatio;
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
        "attribute highp vec2 srcTexCoordAttr;\n"
        "attribute highp vec2 dstTexCoordAttr;\n"
        "varying highp vec2 srcTexCoord;\n"
        "varying highp vec2 dstTexCoord;\n"
        "varying highp vec2 ndcPos;\n"
        "void main()\n"
        "{\n"
        "    srcTexCoord = srcTexCoordAttr;\n"
        "    dstTexCoord = dstTexCoordAttr;\n"
        "    ndcPos = posAttr;\n"
        "    gl_Position = vec4(posAttr, 0, 1);\n"
        "}\n";

static const char *g_fragShaderSource =
        "uniform sampler2D srcSampler;\n"
        "uniform sampler2D dstSampler;\n"
        "uniform lowp float progress;\n"
        "uniform mediump float imgAspectRatio;\n"
        "varying highp vec2 srcTexCoord;\n"
        "varying highp vec2 dstTexCoord;\n"
        "varying highp vec2 ndcPos;\n"
        "void main()\n"
        "{\n"
        "    lowp vec4 srcColor = texture2D(srcSampler, srcTexCoord);\n"
        "    lowp vec4 dstColor = texture2D(dstSampler, dstTexCoord);\n"
        "    highp float longSideLength = max(1.0, imgAspectRatio);\n"
        "    highp float threshold = longSideLength * progress;\n"
        "    highp float radius = length(vec2(ndcPos.x * imgAspectRatio, ndcPos.y));\n"
        "    lowp float factor = step(threshold, radius);\n"
        "    gl_FragColor = mix(dstColor, srcColor, factor);\n"
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
    instanceData->attrLoc_srcTexCoord = glGetAttribLocation(instanceData->shaderProgram, "srcTexCoordAttr");
    instanceData->attrLoc_dstTexCoord = glGetAttribLocation(instanceData->shaderProgram, "dstTexCoordAttr");
    instanceData->uniformLoc_progress = glGetUniformLocation(instanceData->shaderProgram, "progress");
    instanceData->uniformLoc_imgAspectRatio = glGetUniformLocation(instanceData->shaderProgram, "imgAspectRatio");

    glUseProgram(instanceData->shaderProgram);
    const GLint dstSamplerUniformLoc = glGetUniformLocation(instanceData->shaderProgram, "dstSampler");
    glUniform1i(dstSamplerUniformLoc, 1);
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
    status = g_paramSuite->paramGetHandle(paramSetInstance, MfxpVideoEffectTransitionProgressParamName, &paramInstance, nullptr);
    if (status != keMfxpStatOK)
        return status;

    double proress = 1;
    g_paramSuite->paramGetValue(paramInstance, &proress);
    if (fabs(proress) < 0.001)
        g_propSuite->propSetString(outArgs, keMfxpPropName, 0, MfxpVideoEffectTransitionSourceFromClipName);
    else if (fabs(proress - 1) < 0.001)
        g_propSuite->propSetString(outArgs, keMfxpPropName, 0, MfxpVideoEffectTransitionSourceToClipName);

    return keMfxpStatOK;
}

static EMfxpStatus RenderEffect(
        MfxpVideoEffectHandle effectInstance,
        PluginInstanceData *instanceData,
        MfxpPropertySetHandle srcInputVideoFrame,
        MfxpPropertySetHandle dstInputVideoFrame,
        MfxpPropertySetHandle outputVideoFrame)
{
    EMfxpStatus status;

    int srcInputTexId = 0;
    g_propSuite->propGetInt(srcInputVideoFrame, keMfxpPropVideoFrameOpenGLTexId, 0, &srcInputTexId);
    int srcInputTexIsUpsideDown = 0;
    g_propSuite->propGetInt(srcInputVideoFrame, keMfxpPropVideoFrameOpenGLUpsideDownTexture, 0, &srcInputTexIsUpsideDown);

    int dstInputTexId = 0;
    g_propSuite->propGetInt(dstInputVideoFrame, keMfxpPropVideoFrameOpenGLTexId, 0, &dstInputTexId);
    int dstInputTexIsUpsideDown = 0;
    g_propSuite->propGetInt(dstInputVideoFrame, keMfxpPropVideoFrameOpenGLUpsideDownTexture, 0, &dstInputTexIsUpsideDown);

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
    status = g_paramSuite->paramGetHandle(paramSetInstance, MfxpVideoEffectTransitionProgressParamName, &paramInstance, nullptr);
    if (status != keMfxpStatOK)
        return status;

    double progress = 1;
    g_paramSuite->paramGetValue(paramInstance, &progress);

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

    int pixelAspectRatio[2];
    status = g_propSuite->propGetIntN(outputVideoFrame, keMfxpPropVideoFramePixelAspectRatio, 2, pixelAspectRatio);
    if (status != keMfxpStatOK)
        return status;

    const double imgAspectRatio = widthInPixel * double(pixelAspectRatio[0]) / pixelAspectRatio[1] / heightInPixel;

    glUseProgram(instanceData->shaderProgram);
    glUniform1f(instanceData->uniformLoc_progress, GLfloat(progress));
    glUniform1f(instanceData->uniformLoc_imgAspectRatio, GLfloat(imgAspectRatio));

    // Setup frame buffer
    glBindTexture(outputTexTarget, GLuint(outputTexId));
    glTexParameteri(outputTexTarget, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
    glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, outputTexTarget, GLuint(outputTexId), 0);

    glActiveTexture(GL_TEXTURE1);
    glBindTexture(GL_TEXTURE_2D, GLuint(dstInputTexId));
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

    glActiveTexture(GL_TEXTURE0);
    glBindTexture(GL_TEXTURE_2D, GLuint(srcInputTexId));
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

    struct VertexData {
        GLfloat x, y;
        GLfloat ss, st;
        GLfloat ds, dt;
    } vertices[4];

    vertices[0].x = -1;
    vertices[0].y = 1;
    vertices[0].ss = 0;
    vertices[0].st = GLfloat(srcInputTexIsUpsideDown ? 0 : 1);
    vertices[0].ds = 0;
    vertices[0].dt = GLfloat(dstInputTexIsUpsideDown ? 0 : 1);

    vertices[1].x = -1;
    vertices[1].y = -1;
    vertices[1].ss = 0;
    vertices[1].st = GLfloat(srcInputTexIsUpsideDown ? 1 : 0);
    vertices[1].ds = 0;
    vertices[1].dt = GLfloat(dstInputTexIsUpsideDown ? 1 : 0);

    vertices[2].x = 1;
    vertices[2].y = 1;
    vertices[2].ss = 1;
    vertices[2].st = GLfloat(srcInputTexIsUpsideDown ? 0 : 1);
    vertices[2].ds = 1;
    vertices[2].dt = GLfloat(dstInputTexIsUpsideDown ? 0 : 1);

    vertices[3].x = 1;
    vertices[3].y = -1;
    vertices[3].ss = 1;
    vertices[3].st = GLfloat(srcInputTexIsUpsideDown ? 1 : 0);
    vertices[3].ds = 1;
    vertices[3].dt = GLfloat(dstInputTexIsUpsideDown ? 1 : 0);

    glVertexAttribPointer(
                instanceData->attrLoc_pos, 2, GL_FLOAT, GL_FALSE,
                GLsizei(sizeof(VertexData)),
                reinterpret_cast<const GLvoid *>(&vertices[0].x));
    glVertexAttribPointer(
                instanceData->attrLoc_srcTexCoord, 2, GL_FLOAT, GL_FALSE,
                GLsizei(sizeof(VertexData)),
                reinterpret_cast<const GLvoid *>(&vertices[0].ss));
    glVertexAttribPointer(
                instanceData->attrLoc_dstTexCoord, 2, GL_FLOAT, GL_FALSE,
                GLsizei(sizeof(VertexData)),
                reinterpret_cast<const GLvoid *>(&vertices[0].ds));

    glEnableVertexAttribArray(instanceData->attrLoc_pos);
    glEnableVertexAttribArray(instanceData->attrLoc_srcTexCoord);
    glEnableVertexAttribArray(instanceData->attrLoc_dstTexCoord);

    glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);

    glDisableVertexAttribArray(instanceData->attrLoc_pos);
    glEnableVertexAttribArray(instanceData->attrLoc_srcTexCoord);
    glEnableVertexAttribArray(instanceData->attrLoc_dstTexCoord);

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
    // Get source input video frame
    //
    MfxpVideoEffectPinHandle srcInputPinInstance;
    status = g_videoEffectSuite->pinGetHandle(effectInstance, MfxpVideoEffectTransitionSourceFromClipName, &srcInputPinInstance, nullptr);
    if (status != keMfxpStatOK)
        return status;

    MfxpPropertySetHandle srcInputVideoFrame;
    status = g_videoEffectSuite->pinGetVideoFrame(srcInputPinInstance, &srcInputVideoFrame);
    if (status != keMfxpStatOK)
        return status;

    //
    // Get destination input video frame
    //
    MfxpVideoEffectPinHandle dstInputPinInstance;
    status = g_videoEffectSuite->pinGetHandle(effectInstance, MfxpVideoEffectTransitionSourceToClipName, &dstInputPinInstance, nullptr);
    if (status != keMfxpStatOK) {
        g_videoEffectSuite->pinReleaseVideoFrame(srcInputVideoFrame);
        return status;
    }

    MfxpPropertySetHandle dstInputVideoFrame;
    status = g_videoEffectSuite->pinGetVideoFrame(dstInputPinInstance, &dstInputVideoFrame);
    if (status != keMfxpStatOK) {
        g_videoEffectSuite->pinReleaseVideoFrame(srcInputVideoFrame);
        return status;
    }

    //
    // Get output video frame
    //
    MfxpVideoEffectPinHandle outputPinInstance;
    status = g_videoEffectSuite->pinGetHandle(effectInstance, MfxpVideoEffectOutputPinName, &outputPinInstance, nullptr);
    if (status != keMfxpStatOK) {
        g_videoEffectSuite->pinReleaseVideoFrame(srcInputVideoFrame);
        g_videoEffectSuite->pinReleaseVideoFrame(dstInputVideoFrame);
        return status;
    }

    MfxpPropertySetHandle outputVideoFrame;
    status = g_videoEffectSuite->pinGetVideoFrame(outputPinInstance, &outputVideoFrame);
    if (status != keMfxpStatOK) {
        g_videoEffectSuite->pinReleaseVideoFrame(srcInputVideoFrame);
        g_videoEffectSuite->pinReleaseVideoFrame(dstInputVideoFrame);
        return status;
    }

    status = RenderEffect(effectInstance, instanceData, srcInputVideoFrame, dstInputVideoFrame, outputVideoFrame);
    g_videoEffectSuite->pinReleaseVideoFrame(srcInputVideoFrame);
    g_videoEffectSuite->pinReleaseVideoFrame(dstInputVideoFrame);
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

MfxpPlugin g_circleInPlugin =
{
    keMfxpApiType_VideoEffect,  // Indicate this is a video effect plugin
    "com.meishesdk.circleIn",   // Unique name to identify this plugin
    1,                          // Major version of this plugin
    0,                          // Minor version of this plugin
    &PluginSetHost,
    &PluginEntryPoint
};

