//================================================================================
//
// (c) Copyright China Digital Video (Beijing) Limited, 2020. All rights reserved.
//
// This code and information is provided "as is" without warranty of any kind,
// either expressed or implied, including but not limited to the implied
// warranties of merchantability and/or fitness for a particular purpose.
//
//--------------------------------------------------------------------------------
//   Birth Date:    Aug 07. 2020
//   Author:        Meishe video team
//================================================================================
#pragma once

#include "MfxpCore.h"
#include "MfxpParam.h"

#ifdef __cplusplus
extern "C" {
#endif

/*! \file MfxpVideoFx.h
 *  Video effect related definition of Meishe FX plugin
 */

/** @brief Opaque declaration of an Meishe FX plugin video effect descriptor/instance */
typedef struct MfxpVideoEffectStruct *MfxpVideoEffectHandle;

/** @brief Opaque declaration of an Meishe FX plugin video effect pin descriptor/instance */
typedef struct MfxpVideoEffectPinStruct *MfxpVideoEffectPinHandle;

/** @brief Video effect type enumeration */
enum EMfxpVideoEffectType
{
    keMfxpVideoEffectType_Invalid = -1,
    keMfxpVideoEffectType_Filter = 0,
    keMfxpVideoEffectType_Transition
};

/** @brief Video effect render context enumeration */
enum EMfxVideoEffectRenderContext
{
    keMfxpVideoEffectRenderContext_Host = 0,
    keMfxpVideoEffectRenderContext_OpenGL,
    keMfxpVideoEffectRenderContext_Metal,
    keMfxpVideoEffectRenderContext_Vulkan
};

/** @brief Video frame's memory location enumeration */
enum EMfxVideoFrameMemoryLocation
{
    keMfxpVideoFrameMemoryLocation_Invalid = -1,
    /** @brief Indicates CPU addressable memory */
    keMfxpVideoFrameMemoryLocation_Host,
    /** @brief Indicates GPU addressable memory, e.g. texture */
    keMfxpVideoFrameMemoryLocation_GPU
};

/** @brief video frame(in CPU memory) pixel format enumeration */
enum EMfxpPixelFormat
{
    keMfxpPixelFormat_Invalid = -1,
    keMfxpPixelFormat_RGBA = 0,
    keMfxpPixelFormat_BGRA,
    keMfxpPixelFormat_RGB,
    keMfxpPixelFormat_BGR,
    keMfxpPixelFormat_YUV420,
    keMfxpPixelFormat_NV12,
    keMfxpPixelFormat_NV21,
    keMfxpPixelFormat_Graysacle
};

/** @brief Pixel bit depth enumeration */
enum EMfxpPixelBitDepth
{
    keMfxPixelBitDepth_Invalid = -1,
    keMfxPixelBitDepth_Uint8 = 0,
    keMfxPixelBitDepth_Float16,
    keMfxPixelBitDepth_Float32
};

/** @brief OpenGL texture Y axis direction enumeration */
enum EMfxpOpenGLTextureYDir
{
    /** @brief Both normal and invert are accepted */
    keMfxpOpenGLTextureYDir_Any = 0,
    /** @brief Comply with OpenGL spec, Y axis point upward, (0,0) is bottom left corner of the texture image */
    keMfxpOpenGLTextureYDir_Normal,
    /** @brief Inverted vetcially, Y axis point downward, (0,0) is top left corner of the texture image */
    keMfxpOpenGLTextureYDir_Inverted
};

/** @brief String that is the name of the standard Meishe FX plugin video effect output pin */
#define MfxpVideoEffectOutputPinName "Output"

/** @brief String that is the name of the standard Meishe FX plugin video effect single source input pin */
#define MfxpVideoEffectSimpleSourcePinName "Source"

/** @brief String that is the name of the 'from' pin in the Meishe FX plugin video transition effect */
#define MfxpVideoEffectTransitionSourceFromClipName "SourceFrom"

/** @brief String that is the name of the 'to' pin in the Meishe FX plugin video transition effect */
#define MfxpVideoEffectTransitionSourceToClipName "SourceTo"

/** @brief String that is the name of the mandated progress parameter in a Meishe FX plugin video transition effect */
#define MfxpVideoEffectTransitionProgressParamName "Progress"


/** @brief The Meishe FX plugin suite for video effects

    This suite provides the functions needed by a plugin to defined and use an video effect plugin.
 */
typedef struct MfxpVideoEffectSuite {
    /** @brief Retrieves the property set for the given video effect descriptor/instance

        \arg videoEffect   video effect to get the property set for
        \arg propHandle    pointer to a the property set pointer, value is returned here

        The property handle valid for the lifetime of the video effect handle.

        @returns
            - ::keMfxpStatOK            - the property set was found and returned
            - ::keMfxpStatErrBadHandle  - if the paramter handle was invalid
            - ::keMfxpStatErrUnknown    - if the type is unknown
    */
    EMfxpStatus (*getPropertySet)(
            MfxpVideoEffectHandle videoEffect,
            MfxpPropertySetHandle *propHandle);

    /** @brief Retrieves the parameter set descriptor/instance for the given video effect descriptor/instance

        \arg videoEffect    video effect to get the property set for
        \arg paramSet       pointer to a the parameter set, value is returned here

        The param set handle is valid for the lifetime of the video effect handle.

        @returns
            - ::keMfxpStatOK            - the property set was found and returned
            - ::keMfxpStatErrBadHandle  - if the paramter handle was invalid
            - ::keMfxpStatErrUnknown    - if the type is unknown
    */
    EMfxpStatus (*getParamSet)(
            MfxpVideoEffectHandle videoEffect,
            MfxpParamSetHandle *paramSet);

    /** @brief Define a pin to the video effect.

        \arg videoEffect    the handle passed into 'describe' action
        \arg name           unique name of the pin to define
        \arg isInputPin     indicate whether this pin is a input or output pin
        \arg propertySet    a property handle for the pin descriptor will be returned here

        This function defines a video effect pin to a host, the returned property set is used to describe
        various aspects of the video effect pin to the host. Note that this does not create a pin instance.

        \pre
            - we are inside the describe action.

        @returns
            - ::keMfxpStatOK            - the pin was defined
            - ::keMfxpStatErrBadHandle  - if the video effect descriptor handle was invalid
            - ::keMfxpStatErrExists     - if the a pin with the same name exists
    */
    EMfxpStatus (*pinDefine)(
            MfxpVideoEffectHandle videoEffect,
            const char *name,
            bool isInputPin,
            MfxpPropertySetHandle *propertySet);

    /** @brief Get the propery handle of the named pin in the given video effect

        \arg videoEffect    video effect descriptor/instance handle
        \arg name           name of the pin previously defined
        \arg pin            where to return the pin handle
        \arg propertySet    if not null, the property set handle for the pin descriptor/instance will be placed here.

        This return a pin descriptor/instance handle for the given video effect descriptor/instance,
        The returned handle will be valid for the life time of the video effect descriptor/instance.
    */
    EMfxpStatus (*pinGetHandle)(
            MfxpVideoEffectHandle videoEffect,
            const char *name,
            MfxpVideoEffectPinHandle *pin,
            MfxpPropertySetHandle *propertySet);

    /** @brief Retrieves the property set for a given video effect pin descriptor/instance

        \arg bin        video effect pin to descriptor/instance get the property set for
        \arg propHandle pointer to a the property set handle, value is returned her

        The property handle is valid for the lifetime of the pin.

        @returns
            - ::keMfxpStatOK            - the property set was found and returned
            - ::keMfxpStatErrBadHandle  - if the paramter handle was invalid
            - ::keMfxpStatErrUnknown    - if the type is unknown
    */
    EMfxpStatus (*pinGetPropertySet)(
            MfxpVideoEffectPinHandle pin,
            MfxpPropertySetHandle *propHandle);

    /** @brief Get a handle for a video frame in a video effect pin instance

        \arg pin                the video effect pin to extract the video frame from
        \arg videoFrameHandle   property set containing the video frame's data

        A video frame is fetched from a video effect pin instance and returned in the videoFrameHandle.

        If pinGetVideoFrame is called twice with the same parameters, then two separate video frame handles will be returned,
        each of which must be released. The underlying implementation could share video frame data pointers and use reference counting to maintain them.

        \pre
            - pin instance was returned by pinGetHandle

        \post
            - video frame handle is only valid for the duration of the action pinGetVideoFrame is called in
            - video frame handle to be disposed of by pinReleaseVideoFrame before the action returns

        @returns
            - ::keMfxpStatOK            - the video frame was successfully fetched and returned in the handle,
            - ::keMfxpStatErrFailed     - the video frame could not be fetched, the plugin should continue operation, but assume the video frame was black and transparent.
            - ::keMfxpStatErrBadHandle  - the pin instance handle was invalid,
            - ::keMfxpStatErrMemory     - the host had not enough memory to complete the operation, plugin should abort whatever it was doing.
    */
    EMfxpStatus (*pinGetVideoFrame)(
            MfxpVideoEffectPinHandle pin,
            MfxpPropertySetHandle *videoFrameHandle);
  
    /** @brief Releases the video frame handle previously returned by pinGetVideoFrame

        \pre
            - videoFrameHandle was returned by pinGetVideoFrame

        \post
            - all operations on videoFrameHandle will be invalid

        @returns
            - ::keMfxpStatOK            - the video frame was successfully released
            - ::keMfxpStatErrBadHandle  - the video frame handle was invalid
    */
    EMfxpStatus (*pinReleaseVideoFrame)(MfxpPropertySetHandle videoFrameHandle);
} MfxpVideoEffectSuite;

#ifdef __cplusplus
}
#endif

