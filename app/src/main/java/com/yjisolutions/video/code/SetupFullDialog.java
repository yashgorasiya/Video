package com.yjisolutions.video.code;

import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.yjisolutions.video.R;

import java.util.Objects;

public class SetupFullDialog {
    public static void setupFullHeight(BottomSheetDialog bottomSheetDialog, int height) {
        FrameLayout bottomSheet = bottomSheetDialog.findViewById(R.id.design_bottom_sheet);
        BottomSheetBehavior<FrameLayout> behavior = BottomSheetBehavior.from(Objects.requireNonNull(bottomSheet));
        ViewGroup.LayoutParams layoutParams = bottomSheet.getLayoutParams();
        if (layoutParams != null) layoutParams.height = height;
        bottomSheet.setLayoutParams(layoutParams);
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

}
