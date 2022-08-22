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
#include "MfxpProperty.h"

#ifdef __cplusplus
extern "C" {
#endif

/*! \file MfxpParam.h
 *  Parameter related definition of Meishe FX plugin
 */

/** @brief Opaque declaration of an Meishe FX plugin parameter set(descriptor or instance) */
typedef struct MfxpParamSetStruct *MfxpParamSetHandle;

/** @brief Opaque declaration of an Meishe FX plugin parameter(descriptor or instance) */
typedef struct MfxpParamStruct *MfxpParamHandle;

/** @brief Parameter type enumeration */
enum EMfxpParamType
{
    /** @brief Identify a parameter as a single valued integer */
    keMfxpParamType_Integer = 0,
    /** @brief Identify a parameter as a single valued floating point number */
    keMfxpParamType_Double,
    /** @brief Identify a parameter as a single valued boolean parameter */
    keMfxpParamType_Boolean,
    /** @brief Identify a parameter as a single valued, 'one-of-many' choice parameter */
    keMfxpParamType_Menu,
    /** @brief Identify a parameter as a Red, Green, Blue and Alpha color parameter */
    keMfxpParamType_Color,
    /** @brief Identify a parameter as a String (UTF8) parameter */
    keMfxpParamType_String
};

/** @brief String parameter mode enumeration */
enum EMfxpStringParamMode
{
    keMfxpStringParamMode_SingleLine = 0,
    keMfxpStringParamMode_MultiLine,
    keMfxpStringParamMode_FilePath,
    keMfxpStringParamMode_DirectoryPath,
    keMfxpStringParamMode_Label
};

/** @brief Suite used to define and manipulate parameters */
typedef struct MfxpParameterSuite {
    /** @brief Defines a new parameter of the given type in a describe action

        \arg paramSet       handle to the parameter set descriptor that will hold this parameter
        \arg name           unique name of the parameter in ASCII
        \arg paramType      type of the parameter to create, please refer to \ref EMfxpParamType
        \arg propertySet    if not null, a pointer to the parameter descriptor's property set will be placed here.

        This function defines a parameter in a parameter set and returns a property set which is used to describe that parameter.

        This function does not actually create a parameter, it only says that one should exist in any subsequent instances. To fetch an
        parameter instance paramGetHandle must be called on an instance.

        This function can always be called in one of a plug-in's "describe" functions which defines the parameter sets common to all instances of a plugin.

        @returns
            - ::keMfxpStatOK             - the parameter was created correctly
            - ::keMfxpStatErrBadHandle   - if the plugin handle was invalid
            - ::keMfxpStatErrExists      - if a parameter of that name exists already in this plugin
            - ::keMfxpStatErrUnknown     - if the type is unknown
            - ::keMfxpStatErrUnsupported - if the type is known but unsupported
    */
    EMfxpStatus (*paramDefine)(
            MfxpParamSetHandle paramSet,
            const char *name,
            EMfxpParamType paramType,
            MfxpPropertySetHandle *propertySet);

    /** @brief Retrieves the handle for a parameter in a given parameter set

        \arg paramSet       handle to the parameter set descriptor/instance to fetch the parameter descriptor/instance handle from
        \arg name           parameter name to ask about
        \arg param          pointer to a parameter decriptor/instance handle, the value is returned here
        \arg propertySet    if not null, a pointer to the parameter's property set will be placed here.

        The paramter descriptor/instance handle is valid for the life-time of parameter set descriptor/instance.

        @returns
            - ::keMfxpStatOK            - the parameter was found and returned
            - ::keMfxpStatErrBadHandle  - if the plugin handle was invalid
            - ::keMfxpStatErrUnknown    - if the type is unknown
    */
    EMfxpStatus (*paramGetHandle)(
            MfxpParamSetHandle paramSet,
            const char *name,
            MfxpParamHandle *param,
            MfxpPropertySetHandle *propertySet);

    /** @brief Retrieves the property set handle for the given parameter set descriptor/instance

        \arg paramSet      parameter set descriptor/instance to get the property set from
        \arg propHandle    pointer to a the property set handle, value is returned here

        \note The property handle belonging to a parameter set is the same as the property handle belonging to the plugin instance.

        @returns
            - ::keMfxpStatOK            - the property set was found and returned
            - ::keMfxpStatErrBadHandle  - if the paramter handle was invalid
            - ::keMfxpStatErrUnknown    - if the type is unknown
    */
    EMfxpStatus (*paramSetGetPropertySet)(
            MfxpParamSetHandle paramSet,
            MfxpPropertySetHandle *propHandle);

    /** @brief Retrieves the property set handle for the given parameter descriptor/instance

        \arg param         parameter descriptor/instance to get the property set from
        \arg propHandle    pointer to a the property set handle, value is returedn her

        The property handle is valid for the lifetime of the parameter, which is the lifetime of the instance that owns the parameter

        @returns
            - ::keMfxpStatOK            - the property set was found and returned
            - ::keMfxpStatErrBadHandle  - if the paramter handle was invalid
            - ::keMfxpStatErrUnknown    - if the type is unknown
    */
    EMfxpStatus (*paramGetPropertySet)(
            MfxpParamHandle param,
            MfxpPropertySetHandle *propHandle);

    /** @brief Gets the current value of a parameter instance

        \arg paramHandle parameter instance handle to fetch value from
        \arg ...         one or more pointers to variables of the relevant type to hold the parameter's value

        This gets the current value of a parameter. The varargs ... argument needs to be pointer to C variables
        of the relevant type for this parameter. Note that params with multiple values (eg color) take
        multiple args here. For example...

        @verbatim
        MfxpParamHandle myDoubleParam, myColorParam;
        mfxpHost->paramGetHandle(instance, "myDoubleParam", &myDoubleParam);
        double myDoubleValue;
        mfxpHost->paramGetValue(myDoubleParam, &myDoubleValue);
        mfxpHost->paramGetHandle(instance, "myColourParam", &myColourParam);
        double myR, myG, myB;
        mfxpHost->paramGetValue(myColourParam, &myR, &myG, &myB);
        @endverbatim

        @returns
            - ::keMfxpStatOK            - all was OK
            - ::keMfxpStatErrBadHandle  - if the parameter handle was invalid
    */
    EMfxpStatus (*paramGetValue)(MfxpParamHandle paramHandle, ...);
} MfxpParameterSuiteV1;

#ifdef __cplusplus
}
#endif

