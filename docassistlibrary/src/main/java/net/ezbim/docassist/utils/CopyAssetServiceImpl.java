package net.ezbim.docassist.utils;

import android.content.Context;

public class CopyAssetServiceImpl implements CopyAsset {
    private Context context;

    public CopyAssetServiceImpl(Context context) {
        this.context = context;
    }

    @Override
    public void copy(String assetName, String destinationPath) {
        CopyAssetService.startCopyAction(context, assetName, destinationPath);
    }
}
