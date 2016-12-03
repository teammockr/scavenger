package ca.dal.cs.scavenger;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.typeface.IIcon;

import java.io.File;

/**
 * Created by odavi on 11/27/2016.
 */

class LoadVisual {
    static VisualLoader withContext(Context context) {
        return new VisualLoader(context);
    }

    static class VisualLoader {
        private Context context;
        private VisualDataSource visualDataSource;
        private IIcon defaultIcon;

        VisualLoader(Context context) {
            this.context = context;
        }

        VisualLoader fromSource(VisualDataSource source) {
            this.visualDataSource = source;
            return this;
        }

        VisualLoader withDefaultIcon(IIcon icon) {
            this.defaultIcon = icon;
            return this;
        }

        void into(ImageView targetView) {
            String localDataPath = visualDataSource.getLocalDataPath();
            String dataURL = visualDataSource.getDataURL();
            if (!(localDataPath == null || localDataPath.isEmpty())) {
                Glide.with(context)
                        .load(new File(localDataPath))
                        .into(targetView);
            } else if (!(dataURL == null || dataURL.isEmpty())) {
                Glide.with(context)
                        .load(dataURL)
                        .into(targetView);
            } else if (defaultIcon != null) {
                targetView.setImageDrawable(
                        new IconicsDrawable(context).icon(defaultIcon));
            } else {
                targetView.setImageDrawable(
                        new IconicsDrawable(context).icon(GoogleMaterial.Icon.gmd_broken_image));
            }
        }
    }
}
