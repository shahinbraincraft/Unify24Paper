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

#ifdef __cplusplus
extern "C" {
#endif

/*! \file MfxpProperty.h
 *  Property related definition of Meishe FX plugin
 */

/** @brief Property identifier enumeration */
enum EMfxpProperty
{
    //
    // General properties
    //
    /** @brief General property used to get/set the time of something. In milliseconds.

        - Type - double X 1
        - Default - 0, if a setable property
        - Property Set - commonly used as an argument to actions, input and output.
    */
    keMfxpPropTime = 0,

    /** @brief General property, used to identify the kind of an object behind a handle

        - Type - ASCII C string X 1
        - Property Set - any object handle (read only)
        - Valid Values - please refer to \ref EMfxpObjectType
    */
    keMfxpPropObjectType,

    /** @brief Unique name of an object.

        - Type - ASCII C string X 1
        - Property Set - on many objects (descriptors and instances) (read only)

        This property is used to label objects uniquely amoung objects of that type. It is typically set when a plugin creates a new object with a function that takes a name.
    */
    keMfxpPropName,

    /** @brief User visible name of an object.

        - Type - UTF8 C string X 1
        - Property Set - on many objects (descriptors and instances). Typically readable and writable in most cases.
        - Default - the ::keMfxpPropName the object was created with.

        The label is what a user sees on any interface in place of the object's name.
    */
    keMfxpPropLabel,

    //
    // Host properties
    //
    /** @brief Property on the host descriptor, saying what Meishe SDK version is being implemented

        - Type - integer X 3
        - Property Set - host descriptor (read only)

        This is a version string that will specify which Meishe SDK version is being implemented by a host.
        For example "2.17.0", "2.18.1"...

        If this is not present, it is safe to assume that the version of the API is "1.0".
    */
    keMfxpPropHostMeisheSDKVersion = 1000,

    /** @brief Indicates video effect render context support by the host

        - Type - integer X N
        - Property Set - host descriptor (read only)
        - Valid Values - Please refer to \ref EMfxVideoEffectRenderContext
    */
    keMfxpPropHostSupportedVideoEffectRenderContexts,

    //
    // Video effect descriptor/instance properties
    //
    /** @brief Indicates to the host the video effect type

       - Type - integer X 1
       - Property Set - video effect descriptor passed to keMfxpActionType_DescribePlugin (read/write)
       - Default - this has no defaults, it must be set
       - Valid Values - Please refer to \ref EMfxpVideoEffectType
    */
    keMfxpPropVideoEffectType = 2000,

    /** @brief Description of the plug-in to a user.

        - Type - string X 1
        - Property Set - plugin descriptor (read/write)
        - Default - ""
        - Valid Values - UTF8 string

        This is a string giving a potentially verbose description of the effect.
    */
    keMfxpPropPluginDescription,

    /** @brief  A private data pointer that the plug-in can store its own data behind.

        - Type - pointer X 1
        - Property Set - plugin instance (read/write),
        - Default - NULL

        This data pointer is unique to each plug-in instance, so two instances of the same plug-in do not share the same data pointer.
        Use it to store any needed private data structures.
    */
    keMfxpPropInstanceData,

    /** @brief Indicates video effect render context support by the video effect

        - Type - integer X N
        - Property Set - video effect descriptor passed to keMfxpActionType_DescribePlugin (read/write)
        - Valid Values - Please refer to \ref EMfxVideoEffectRenderContext
    */
    keMfxpPropVideoEffectSupportedVideoEffectRenderContexts,

    /** @brief Indicates video effect render context

        - Type - integer X 1
        - Property Set - inArgs of CreateInstance action (read only), video effect instance (read only)
        - Valid Values - Please refer to \ref EMfxVideoEffectRenderContext
    */
    keMfxpPropVideoEffectRenderContext,

    /** @brief The duration of the video effect

        - Type - double X 1
        - Property Set - a video effect instance (read only)

        \note You can only get a sensible effect duration during keMfxpActionType_IsIdentity and keMfxpActionType_Render action!

        This contains the duration of the plug-in effect, in milliseconds.
        A value less than or equal to zero indicate unknown duration.
    */
    keMfxpPropVideoEffectDuration,

    /** @brief Indicates the allowed input OpenGL texture Y-axis direction

       - Type - integer X 1
       - Property Set - video effect descriptor passed to keMfxpActionType_DescribePlugin (read/write)
       - Default - ::keMfxpOpenGLTextureYDir_Any
       - Valid Values - Please refer to \ref EMfxpOpenGLTextureYDir

        This property has valid value only when the video frame resides at GPU memory
        and the current video effect render context is OpenGL
    */
    keMfxpPropVideoEffectInputOpenGLTextureYDir,

    //
    // Video effect pin descriptor/instance properties
    //
    /** @brief Indicates whether the video effect pin is input pin

        - Type - integer X 1
        - Property Set - a video effect pin descriptor/instance (read only)
    */
    keMfxpPropVideoEffectPinIsInput = 3000,

    //
    // Parameter set descriptor/instance properties
    //

    //
    // Parameter descriptor/instance properties
    //
    /** @brief The type of a parameter.

        - Type - integer X 1
        - Property Set - plugin parameter descriptor (read only) and instance (read only)

        For parameter type list, please refer to \ref EMfxpParamType
    */
    keMfxpPropParamType = 5000,

    /** @brief The default value of a parameter.

        - Type - The type is dependant on the parameter type as is the dimension.
        - Property Set - plugin parameter descriptor (read/write) and instance (read only),
        - Default - 0 cast to the relevant type (or "" for strings and custom parameters)

        The exact type and dimension is dependant on the type of the parameter. These are....
            - ::keMfxpParamType_Integer - integer property of one dimension
            - ::keMfxpParamType_Double - double property of one dimension
            - ::keMfxpParamType_Boolean - integer property of one dimension
            - ::keMfxpParamType_Menu - integer property of one dimension
            - ::keMfxpParamType_Color -double property of four dimensions
            - ::keMfxpParamType_String - string property of one dimension
    */
    keMfxpPropParamDefault,

    /** @brief Possible options in a menu parameter.

        - Type - UTF8 C string X N
        - Property Set - plugin parameter descriptor (read/write) and instance (read only),
        - Default - the property is empty with no options set.

        This property contains the set of options that will be presented to a user from a menu parameter.
    */
    keMfxpPropParamMenuOptions,

    /** @brief The minimum value for a numeric parameter.

        - Type - int or double X N
        - Property Set - plugin parameter descriptor (read/write) and instance (read only),
        - Default - the smallest possible value corresponding to the parameter type (eg: INT_MIN for an integer, -DBL_MAX for a double parameter)
    */
    keMfxpPropParamMin,

    /** @brief The maximum value for a numeric parameter.

        - Type - int or double X N
        - Property Set - plugin parameter descriptor (read/write) and instance (read only),
        - Default - the largest possible value corresponding to the parameter type (eg: INT_MAX for an integer, DBL_MAX for a double parameter)
    */
    keMfxpPropParamMax,

    /** @brief Used to indicate the type of a string parameter.

        - Type - C string X 1
        - Property Set - plugin string parameter descriptor (read/write) and instance (read only),
        - Default - ::keMfxpStringParamMode_SingleLine
        - Valid Values - This must be one of the following
            - ::keMfxpStringParamMode_SingleLine
            - ::keMfxpStringParamMode_MultiLine
            - ::keMfxpStringParamMode_FilePath
            - ::keMfxpStringParamMode_DirectoryPath
            - ::keMfxpStringParamMode_Label
    */
    keMfxpPropParamStringMode,

    /** @brief Flags whether to disable animation to a parameter

        - Type - integer X 1
        - Property Set - plugin parameter descriptor (read/write) and instance (read only)
        - Default - 0
        - Valid Values - 0 or 1

        A plug-in uses this property to indicate if a parameter is unable to animate.
    */
    keMfxpPropParamDisableAnimaion,

    /** @brief A hint to the user as to how the parameter is to be used.

        - Type - UTF8 C string X 1
        - Property Set - plugin parameter descriptor (read/write) and instance (read/write),
        - Default - ""
    */
    keMfxpPropParamTip,

    //
    // Video frame properties
    //
    /** @brief The pixel aspect ratio of a video frame

        - Type - integer X 2
        - Property Set - video frame (read only)
        - Default - 1, 1
    */
    keMfxpPropVideoFramePixelAspectRatio = 6000,

    /** @brief The proxy scale currently being applied to the video frame

        - Type - integer X 2
        - Property Set - video frame (read only)
    */
    keMfxpPropVideoFrameProxyScale,

    /** @brief The memory location at which a video frame resides

        - Type - integer X 1
        - Property Set - video frame (read only)
        - Valid Values - Please refer to \ref EMfxVideoFrameMemoryLocation
    */
    keMfxpPropVideoFrameMemoryLocation,

    /** @brief Indicates the pixel depth in a video effect pin or video frame

       - Type - integer X 1
       - Property Set - video frame instance (read only)
       - Valid Values - Please refer to \ref EMfxpPixelBitDepth
    */
    keMfxpPropVideoFramePixelDepth,

    /** @brief Indicates the current pixel format in a video effect pin or video frame

       - Type - integer X 1
       - Property Set - video frame instance (read only)
       - Valid Values - Please refer to \ref EMfxpPixelFormat
    */
    keMfxpPropVideoFramePixelFormat,

    /** @brief The bounds of an video frame's pixels.

        - Type - integer X 4
        - Property Set - video frame (read only)

        video frame pixel bounds in pixel coordinates. The order of the values is x1, y1, x2, y2.
        X values are x1 <= X < x2
        Y values are y1 <= Y < y2
    */
    keMfxpPropVideoFrameBounds,

    /** @brief The image plane data pointers of a video frame

        - Type - pointer X N
        - Property Set - video frame (read only)

        The value count of this property depends on video frame's pixel format:
        - N=1 for keMfxpPixelFormat_RGBA
        - N=1 for keMfxpPixelFormat_BGRA
        - N=1 for keMfxpPixelFormat_RGB
        - N=1 for keMfxpPixelFormat_BGR
        - N=3 for keMfxpPixelFormat_YUV420
        - N=3 for keMfxpPixelFormat_NV12
        - N=3 for keMfxpPixelFormat_NV21

        This property has valid value only when the video frame resides at host memory
    */
    keMfxpPropVideoFramePlanesData,

    /** @brief The image plane stride of a video frame

        - Type - pointer X N
        - Property Set - video frame (read only)

        The value count of this property depends on video frame's pixel format:
        - N=1 for keMfxpPixelFormat_RGBA
        - N=1 for keMfxpPixelFormat_BGRA
        - N=1 for keMfxpPixelFormat_RGB
        - N=1 for keMfxpPixelFormat_BGR
        - N=3 for keMfxpPixelFormat_YUV420
        - N=3 for keMfxpPixelFormat_NV12
        - N=3 for keMfxpPixelFormat_NV21

        Stride is the memory difference between two successive image lines(in bytes), maybe negative value!
        This property has valid value only when the video frame resides at host memory
    */
    keMfxpPropVideoFramePlanesStride,

    /** @brief The OpenGL texture identifier of a video frame

        - Type - integer X 1
        - Property Set - video frame (read only)

        This property has valid value only when the video frame resides at GPU memory
        and the current video effect render context is OpenGL
    */
    keMfxpPropVideoFrameOpenGLTexId,

    /** @brief Indicates whether OpenGL texture image of a video frame invert in Y axis

        - Type - integer X 1
        - Property Set - input video frame (read only), output video frame (read/write)

        This property has valid value only when the video frame resides at GPU memory
        and the current video effect render context is OpenGL
    */
    keMfxpPropVideoFrameOpenGLUpsideDownTexture,

    /** @brief The OpenGL texture target of a video frame

        - Type - integer X 1
        - Property Set - video frame (read only)

        Possible values are GL_TEXTURE_2D and GL_TEXTURE_RECTANGLE
        \note OpenGL texture target of input pin is guaranteed to be GL_TEXTURE_2D

        This property has valid value only when the video frame resides at GPU memory
        and the current video effect render context is OpenGL
    */
    keMfxpPropVideoFrameOpenGLTexTarget,

    //
    // User defined properties
    //
    /** @brief First user property identifier */
    keMfxpPropUserFirst = 50000
};

/** @brief The suite used to access properties on Meishe FX plugin objects */
typedef struct MfxpPropertySuite {
    /** @brief Set a single value in a pointer property

        \arg properties is the handle of the thing holding the property
        \arg property is the property identifier, please refer to \ref EMfxpProperty
        \arg index is for multidimenstional properties and is dimension of the one we are setting
        \arg value is the pointer value of the property we are setting

        @returns
            - ::keMfxpStatOK
            - ::keMfxpStatErrBadHandle
            - ::keMfxpStatErrUnknown
            - ::keMfxpStatErrBadIndex
            - ::keMfxpStatErrValue
    */
    EMfxpStatus (*propSetPointer)(MfxpPropertySetHandle properties, EMfxpProperty property, int index, void *value);

    /** @brief Set a single value in a string property

        \arg properties is the handle of the thing holding the property
        \arg property is the property identifier, please refer to \ref EMfxpProperty
        \arg index is for multidimenstional properties and is dimension of the one we are setting
        \arg value is the string value of the property we are setting

        @returns
            - ::keMfxpStatOK
            - ::keMfxpStatErrBadHandle
            - ::keMfxpStatErrUnknown
            - ::keMfxpStatErrBadIndex
            - ::keMfxpStatErrValue
    */
    EMfxpStatus (*propSetString)(MfxpPropertySetHandle properties, EMfxpProperty property, int index, const char *value);

    /** @brief Set a single value in a double property

        \arg properties is the handle of the thing holding the property
        \arg property is the property identifier, please refer to \ref EMfxpProperty
        \arg index is for multidimenstional properties and is dimension of the one we are setting
        \arg value is the double value of the property we are setting

        @returns
            - ::keMfxpStatOK
            - ::keMfxpStatErrBadHandle
            - ::keMfxpStatErrUnknown
            - ::keMfxpStatErrBadIndex
            - ::keMfxpStatErrValue
    */
    EMfxpStatus (*propSetDouble)(MfxpPropertySetHandle properties, EMfxpProperty property, int index, double value);

    /** @brief Set a single value in  an int property

        \arg properties is the handle of the thing holding the property
        \arg property is the property identifier, please refer to \ref EMfxpProperty
        \arg index is for multidimenstional properties and is dimension of the one we are setting
        \arg value is the integer value of the property we are setting

        @returns
            - ::keMfxpStatOK
            - ::keMfxpStatErrBadHandle
            - ::keMfxpStatErrUnknown
            - ::keMfxpStatErrBadIndex
            - ::keMfxpStatErrValue
    */
    EMfxpStatus (*propSetInt)(MfxpPropertySetHandle properties, EMfxpProperty property, int index, int value);

    /** @brief Set multiple values of the pointer property

        \arg properties is the handle of the thing holding the property
        \arg property is the property identifier, please refer to \ref EMfxpProperty
        \arg count is the number of values we are setting in that property (ie: indicies 0..count-1)
        \arg value is a pointer to an array of pointer property values

        @returns
            - ::keMfxpStatOK
            - ::keMfxpStatErrBadHandle
            - ::keMfxpStatErrUnknown
            - ::keMfxpStatErrBadIndex
            - ::keMfxpStatErrValue
    */
    EMfxpStatus (*propSetPointerN)(MfxpPropertySetHandle properties, EMfxpProperty property, int count, const void **value);

    /** @brief Set multiple values of a string property

        \arg properties is the handle of the thing holding the property
        \arg property is the property identifier, please refer to \ref EMfxpProperty
        \arg count is the number of values we are setting in that property (ie: indicies 0..count-1)
        \arg value is a pointer to an array of string property values

        @returns
            - ::keMfxpStatOK
            - ::keMfxpStatErrBadHandle
            - ::keMfxpStatErrUnknown
            - ::keMfxpStatErrBadIndex
            - ::keMfxpStatErrValue
    */
    EMfxpStatus (*propSetStringN)(MfxpPropertySetHandle properties, EMfxpProperty property, int count, const char **value);

    /** @brief Set multiple values of  a double property

        \arg properties is the handle of the thing holding the property
        \arg property is the property identifier, please refer to \ref EMfxpProperty
        \arg count is the number of values we are setting in that property (ie: indicies 0..count-1)
        \arg value is a pointer to an array of double property values

        @returns
            - ::keMfxpStatOK
            - ::keMfxpStatErrBadHandle
            - ::keMfxpStatErrUnknown
            - ::keMfxpStatErrBadIndex
            - ::keMfxpStatErrValue
    */
    EMfxpStatus (*propSetDoubleN)(MfxpPropertySetHandle properties, EMfxpProperty property, int count, const double *value);

    /** @brief Set multiple values of an int property

        \arg properties is the handle of the thing holding the property
        \arg property is the property identifier, please refer to \ref EMfxpProperty
        \arg count is the number of values we are setting in that property (ie: indicies 0..count-1)
        \arg value is a pointer to an array of integer property values

        @returns
            - ::keMfxpStatOK
            - ::keMfxpStatErrBadHandle
            - ::keMfxpStatErrUnknown
            - ::keMfxpStatErrBadIndex
            - ::keMfxpStatErrValue
    */
    EMfxpStatus (*propSetIntN)(MfxpPropertySetHandle properties, EMfxpProperty property, int count, const int *value);
  
    /** @brief Get a single value from a pointer property

        \arg properties is the handle of the thing holding the property
        \arg property is the property identifier, please refer to \ref EMfxpProperty
        \arg index refers to the index of a multi-dimensional property
        \arg value is a pointer the return location

        @returns
            - ::keMfxpStatOK
            - ::keMfxpStatErrBadHandle
            - ::keMfxpStatErrUnknown
            - ::keMfxpStatErrBadIndex
    */
    EMfxpStatus (*propGetPointer)(MfxpPropertySetHandle properties, EMfxpProperty property, int index, void **value);

    /** @brief Get a single value of a string property

        \arg properties is the handle of the thing holding the property
        \arg property is the property identifier, please refer to \ref EMfxpProperty
        \arg index refers to the index of a multi-dimensional property
        \arg value is a pointer the return location

        @returns
            - ::keMfxpStatOK
            - ::keMfxpStatErrBadHandle
            - ::keMfxpStatErrUnknown
            - ::keMfxpStatErrBadIndex
    */
    EMfxpStatus (*propGetString)(MfxpPropertySetHandle properties, EMfxpProperty property, int index, char **value);

    /** @brief Get a single value of a double property

        \arg properties is the handle of the thing holding the property
        \arg property is the property identifier, please refer to \ref EMfxpProperty
        \arg index refers to the index of a multi-dimensional property
        \arg value is a pointer the return location

        You can use the returned string safely only before the next Meishe FX plugin function call.
        If you want to use it later you must duplicate the string yourself!

        @returns
            - ::keMfxpStatOK
            - ::keMfxpStatErrBadHandle
            - ::keMfxpStatErrUnknown
            - ::keMfxpStatErrBadIndex
    */
    EMfxpStatus (*propGetDouble)(MfxpPropertySetHandle properties, EMfxpProperty property, int index, double *value);

    /** @brief Get a single value of an int property

        \arg properties is the handle of the thing holding the property
        \arg property is the property identifier, please refer to \ref EMfxpProperty
        \arg index refers to the index of a multi-dimensional property
        \arg value is a pointer the return location

        @returns
            - ::keMfxpStatOK
            - ::keMfxpStatErrBadHandle
            - ::keMfxpStatErrUnknown
            - ::keMfxpStatErrBadIndex
    */
    EMfxpStatus (*propGetInt)(MfxpPropertySetHandle properties, EMfxpProperty property, int index, int *value);

    /** @brief Get multiple values of a pointer property

        \arg properties is the handle of the thing holding the property
        \arg property is the property identifier, please refer to \ref EMfxpProperty
        \arg count is the number of values we are getting of that property (ie: indicies 0..count-1)
        \arg value is a pointer to an array of where we will return the property values

        @returns
            - ::keMfxpStatOK
            - ::keMfxpStatErrBadHandle
            - ::keMfxpStatErrUnknown
            - ::keMfxpStatErrBadIndex
    */
    EMfxpStatus (*propGetPointerN)(MfxpPropertySetHandle properties, EMfxpProperty property, int count, void **value);

    /** @brief Get multiple values of a string property

        \arg properties is the handle of the thing holding the property
        \arg property is the property identifier, please refer to \ref EMfxpProperty
        \arg count is the number of values we are getting of that property (ie: indicies 0..count-1)
        \arg value is a pointer to an array of where we will return the property values

        You can use the returned strings safely only before the next Meishe FX plugin function call.
        If you want to use it later you must duplicate the string yourself!

        @returns
            - ::keMfxpStatOK
            - ::keMfxpStatErrBadHandle
            - ::keMfxpStatErrUnknown
            - ::keMfxpStatErrBadIndex
    */
    EMfxpStatus (*propGetStringN)(MfxpPropertySetHandle properties, EMfxpProperty property, int count, char **value);

    /** @brief Get multiple values of a double property

        \arg properties is the handle of the thing holding the property
        \arg property is the property identifier, please refer to \ref EMfxpProperty
        \arg count is the number of values we are getting of that property (ie: indicies 0..count-1)
        \arg value is a pointer to an array of where we will return the property values

        @returns
            - ::keMfxpStatOK
            - ::keMfxpStatErrBadHandle
            - ::keMfxpStatErrUnknown
            - ::keMfxpStatErrBadIndex
    */
    EMfxpStatus (*propGetDoubleN)(MfxpPropertySetHandle properties, EMfxpProperty property, int count, double *value);

    /** @brief Get multiple values of an int property

        \arg properties is the handle of the thing holding the property
        \arg property is the property identifier, please refer to \ref EMfxpProperty
        \arg count is the number of values we are getting of that property (ie: indicies 0..count-1)
        \arg value is a pointer to an array of where we will return the property values

        @returns
            - ::keMfxpStatOK
            - ::keMfxpStatErrBadHandle
            - ::keMfxpStatErrUnknown
            - ::keMfxpStatErrBadIndex
    */
    EMfxpStatus (*propGetIntN)(MfxpPropertySetHandle properties, EMfxpProperty property, int count, int *value);

    /** @brief Resets all dimensions of a property to its default value

        \arg properties is the handle of the thing holding the property
        \arg property is the property identifier we are resetting, please refer to \ref EMfxpProperty

        @returns
            - ::keMfxpStatOK
            - ::keMfxpStatErrBadHandle
            - ::keMfxpStatErrUnknown
    */
    EMfxpStatus (*propReset)(MfxpPropertySetHandle properties, EMfxpProperty property);

    /** @brief Gets the dimension of the property

        \arg properties is the handle of the thing holding the property
        \arg property is the property identifier, please refer to \ref EMfxpProperty
        \arg count is a pointer to an integer where the value is returned

        @returns
            - ::keMfxpStatOK
            - ::keMfxpStatErrBadHandle
            - ::keMfxpStatErrUnknown
    */
    EMfxpStatus (*propGetDimension)(MfxpPropertySetHandle properties, EMfxpProperty property, int *count);
} MfxpPropertySuite;

#ifdef __cplusplus
}
#endif

