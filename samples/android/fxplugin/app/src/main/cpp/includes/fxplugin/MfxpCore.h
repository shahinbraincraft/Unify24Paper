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

#include <stddef.h>
#include <stdint.h>

#ifdef __cplusplus
extern "C" {
#endif

/*! \file MfxpCore.h
 *  Core definition of Meishe FX plugin
 */

/** @brief Platform independent export macro.
 *
 * This macro is to be used before any symbol that is to be
 * exported from a plug-in. This is OS/compiler dependent.
 */
#if defined(_MSC_VER)
# define MfxpExport __declspec(dllexport)
#else
# define MfxpExport __attribute__((visibility("default")))
#endif

/** @brief Blind data structure to manipulate sets of properties through */
typedef struct MfxpPropertySetStruct * MfxpPropertySetHandle;

/** @brief Suite type enumeration */
enum EMfxpSuiteType
{
    /** @brief Suite used to handle properties */
    keMfxpSuiteType_Property = 0,
    /** @brief Suite used to handle parameter set and parameter */
    keMfxpSuiteType_Param,
    /** @brief Suite used to handle video effect processing */
    keMfxpSuiteType_VideoEffect
};

/** @brief Generic host structure passed to MfxpPlugin::setHost function

    This structure contains what is needed by a plug-in to bootstrap its connection
    to the host.
*/
typedef struct MfxpHost {
    /** @brief Global handle to the host. Extract relevant host properties from this.
        This pointer will be valid while the binary containing the plug-in is loaded.
    */
    MfxpPropertySetHandle host;

    /** @brief The function which the plug-in uses to fetch suites from the host.

        \arg \e host          - the host the suite is being fetched from this \em must be the \e host member of the MfxpHost struct containing fetchSuite.
        \arg \e suiteType     - suite type the plugin want to fetch from the host, please refer to @ref EMfxpSuiteType

        Any API fetched will be valid while the binary containing the plug-in is loaded.

        Repeated calls to fetchSuite with the same parameters will return the same pointer.

        @returns
            - NULL if the API is unknown
            - pointer to the relevant API if it was found
    */
    void * (*fetchSuite)(MfxpPropertySetHandle host, EMfxpSuiteType suiteType);
} MfxpHost;

/** @brief Action type enumeration */
enum EMfxpActionType
{
    keMfxpActionType_LoadPlugin = 0,
    keMfxpActionType_UnloadPlugin,
    keMfxpActionType_DescribePlugin,
    keMfxpActionType_CreateInstance,
    keMfxpActionType_DestroyInstance,
    keMfxpActionType_InitInstance,
    keMfxpActionType_CleanupInstance,
    keMfxpActionType_ReleaseCachedResources,
    keMfxpActionType_IsIdentity,
    keMfxpActionType_Render
};

/** @brief Status code enumeration */
enum EMfxpStatus
{
    /** @brief Status code indicating all was fine */
    keMfxpStatOK = 0,
    /** @brief Status error code for a failed operation */
    keMfxpStatErrFailed,
    /** @brief Status error code for a fatal error

        Only returned in the case where the plug-in or host cannot continue to function and needs to be restarted.
     */
    keMfxpStatErrFatal,
    /** @brief Status error code for an operation on or request for an unknown object */
    keMfxpStatErrUnknown,
    /** @brief Status error code returned by plug-ins when they are missing host functionality, either an API or some optional functionality */
    keMfxpStatErrMissingHostFeature,
    /** @brief Status error code for an unsupported feature/operation */
    keMfxpStatErrUnsupported,
    /** @brief Status error code for an operation attempting to create something that exists */
    keMfxpStatErrExists,
    /** @brief Status error code for an incorrect format */
    keMfxpStatErrFormat,
    /** @brief Status error code indicating that something failed due to memory shortage */
    keMfxpStatErrMemory,
    /** @brief Status error code for an operation on a bad handle */
    keMfxpStatErrBadHandle,
    /** @brief Status error code indicating that a given index was invalid or unavailable */
    keMfxpStatErrBadIndex,
    /** @brief Status error code indicating that something failed due an illegal value */
    keMfxpStatErrValue
};

/** @brief Entry point for plug-ins

    \arg \e actionType  - action type, please refer to \ref EMfxpActionType
    \arg \e handle      - object to which action should be applied, this will need to be cast to the appropriate opaque data type depending on the \e actionType
    \arg \e inArgs      - property handle that contains action specific properties
    \arg \e outArgs     - property handle where the plug-in should set various action specific properties

    This is how the host generally communicates with a plug-in. Entry points are used to pass messages
    to various objects used within Meishe FX plugin. The main use is within the MfxpPlugin struct.

    The exact set of actions is determined by the plug-in API that is being implemented, however all plug-ins
    can perform several actions.
 */
typedef EMfxpStatus (* MfxpPluginEntryPoint)(
        EMfxpActionType actionType,
        const void *handle,
        MfxpPropertySetHandle inArgs,
        MfxpPropertySetHandle outArgs);

/** @brief Meishe FX plugin API type enumeration */
enum EMfxpApiType
{
    keMfxpApiType_VideoEffect = 0
};

/** @brief The structure that defines a plug-in to a host.
 *
 *  This structure is the first element in any plug-in structure
 *  using the Mfxp plug-in architecture. By examining its members
 *  a host can determine the API that the plug-in implements,
 *  its name and version.
 *
 */
typedef struct MfxpPlugin {
    /** Defines the type of the plug-in, this will tell the host what the plug-in does.
        e.g.: an video effects plug-in would be a keMfxpApiType_VideoEffect
    */
    EMfxpApiType pluginApiType;

    /** String that uniquely labels the plug-in among all plug-ins that implement an API.
        It need not necessarily be human sensible, however the preference is to use reverse
        internet domain name of the developer, followed by a '.' then by a name that represents
        the plug-in.. It must be a legal ASCII string and have no whitespace in the
        name and no non printing chars.
        For example "com.meishesdk.fastblur"
    */
    const char *pluginIdentifier;

    /** Major version of this plug-in, this gets incremented when backwards compatibility is broken. */
    unsigned int pluginVersionMajor;

    /** Minor version of this plug-in, this gets incremented when software is changed,
        but does not break backwards compatibility. */
    unsigned int pluginVersionMinor;

    /** @brief Function the host uses to connect the plug-in to the host's api fetcher

        \arg \e host - pointer to host's API fetcher

        Mandatory function.

        The very first function called in a plug-in. The plug-in \em must \em not call any Meishe FX plugin functions
        within this, it must only set its local copy of the host pointer.

        \pre
        - nothing else has been called

        \post
        - the pointer suite is valid until the plug-in is unloaded
    */
    void (*setHost)(MfxpHost *host);

    /** @brief Main entry point for plug-ins

        Mandatory function.

        The exact set of actions is determined by the plug-in API that is being implemented, however all plug-ins
        can perform several actions.

        Preconditions
          - setHost has been called
    */
    MfxpPluginEntryPoint mainEntry;
} MfxpPlugin;

/** @brief Defines the number of plug-ins implemented inside a binary
 *
 *  A host calls this to determine how many plug-ins there are inside
 *  a binary it has loaded. A function of this type
 *  must be implemented in and exported from each plug-in binary.
 */
typedef int (* MfxpGetNumberOfPlugins)(void);

/** @brief Returns the 'nth' plug-in implemented inside a binary
 *
 *  Returns a pointer to the 'nth' plug-in implemented in the binary. A function of this type
 *  must be implemented in and exported from each plug-in binary.
 */
typedef const MfxpPlugin * (* MfxpGetPlugin)(int nth);

/** @brief Object type enumeration */
enum EMfxpObjectType
{
    keMfxpObjectType_Host = 0,
    keMfxpObjectType_VideoEffectDesc,
    keMfxpObjectType_VideoEffectInstance,
    keMfxpObjectType_VideoEffectPinDesc,
    keMfxpObjectType_VideoEffectPinInstance,
    keMfxpObjectType_ParameterSetDesc,
    keMfxpObjectType_ParameterSetInstance,
    keMfxpObjectType_ParameterDesc,
    keMfxpObjectType_ParameterInstance,
    keMfxpObjectType_VideoFrame
};

/** @brief How time is specified within the Meishe FX plugin API */
typedef double MfxpTime;

/** @brief Defines one dimensional integer bounds */
typedef struct MfxpRangeI {
    int min, max;
} MfxpRangeI;

/** @brief Defines one dimensional double bounds */
typedef struct MfxpRangeD {
    double min, max;
} MfxpRangeD;

/** @brief Defines two dimensional integer point */
typedef struct MfxpPointI {
    int x, y;
} MfxpPointI;

/** @brief Defines two dimensional double point */
typedef struct MfxpPointD {
    double x, y;
} MfxpPointD;

/** @brief Defines two dimensional integer region

    Regions are x1 <= x < x2
 */
typedef struct MfxpRectI {
    int x1, y1, x2, y2;
} MfxpRectI;

/** @brief Defines two dimensional double region

    Regions are x1 <= x < x2
 */
typedef struct MfxpRectD {
    double x1, y1, x2, y2;
} MfxpRectD;

/** @brief Structure to define an image buffer(in CPU memory)
 */
#define MFXP_IMAGE_BUFFER_MAX_PLANE     4

/** @brief Structure to represent a host image buffer */
typedef struct MfxpImageBuffer {
    /** @brief buffer pointer of each image plane */
    void *data[MFXP_IMAGE_BUFFER_MAX_PLANE];
    /** @brief scanline stride in bytes of each image plane, maybe negative value */
    int stride[MFXP_IMAGE_BUFFER_MAX_PLANE];
} MfxpImageBuffer;

#ifdef __cplusplus
}
#endif

