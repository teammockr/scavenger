//created by odavison
package ca.dal.cs.scavenger;

import android.content.Context;
import android.graphics.Color;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.typeface.IIcon;

import java.io.File;

//Interface for objects that can be loaded into an ImageView
//by a VisualLoader
interface VisualDataSource {
    String getLocalDataPath();
    String getDataURL();
    boolean isComplete();
}

// Image-loading class for objects that implement the VisualDataSource interface
class LoadVisual {
    // Set context for the loader
    static VisualLoader withContext(Context context) {
        return new VisualLoader(context);
    }

    // Inner class to allow for 'builder-style' use of LoadVisual
    static class VisualLoader {
        private Context context;
        private VisualDataSource visualDataSource;
        private IIcon defaultIcon;

        VisualLoader(Context context) {
            this.context = context;
        }

        // Set object to load image path or URL from
        VisualLoader fromSource(VisualDataSource source) {
            this.visualDataSource = source;
            return this;
        }

        // Set a default icon in case visualDataSource contains no image paths
        VisualLoader withDefaultIcon(IIcon icon) {
            this.defaultIcon = icon;
            return this;
        }

        // Set the image or icon for targetView.
        // First try local image files, then fall back, in order, to remote image URL,
        // default icon, and then 'no image found' icon.
        void into(ImageView targetView) {
            String localDataPath = visualDataSource.getLocalDataPath();
            String dataURL = visualDataSource.getDataURL();
            if (!(localDataPath == null || localDataPath.isEmpty()) && localDataPath.endsWith(".jpg")) {
                Glide.with(context)
                        .load(new File(localDataPath))
                        .into(targetView);
            } else if (!(dataURL == null || dataURL.isEmpty()) && dataURL.endsWith(".jpg")) {
                Glide.with(context)
                        .load(dataURL)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(targetView);
            } else if (defaultIcon != null) {
                IconicsDrawable icon = new IconicsDrawable(context).icon(defaultIcon);
                if(visualDataSource.isComplete()) {
                    icon.color(Color.GREEN);
                }
                targetView.setImageDrawable(icon);
            } else {
                targetView.setImageDrawable(
                        new IconicsDrawable(context).icon(GoogleMaterial.Icon.gmd_broken_image));
            }
        }
    }
}
