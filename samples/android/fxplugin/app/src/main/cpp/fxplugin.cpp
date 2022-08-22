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


extern MfxpPlugin g_saturationPlugin;
extern MfxpPlugin g_circleInPlugin;

static const MfxpPlugin *g_plugins[] =
{
    &g_saturationPlugin,
    &g_circleInPlugin
};

extern "C" MfxpExport int mfxpGetNumberOfPlugins(void)
{
    return int(sizeof(g_plugins) / sizeof(g_plugins[0]));
}

extern "C" MfxpExport const MfxpPlugin * mfxpGetPlugin(int nth)
{
    if (nth < 0 || nth >= mfxpGetNumberOfPlugins())
        return nullptr;
    return g_plugins[nth];
}

