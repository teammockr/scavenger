package ca.dal.cs.scavenger;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.view.MenuItem;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
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

        void into(final MenuItem targetView) {
            SimpleTarget<Bitmap> target = new SimpleTarget<Bitmap>(100,100) {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                    targetView.setIcon(new BitmapDrawable(context.getResources(), resource));
                }
            };

            String localDataPath = visualDataSource.getLocalDataPath();
            String dataURL = visualDataSource.getDataURL();
            if (!(localDataPath == null || localDataPath.isEmpty())) {
                Glide.with(context)
                        .load(new File(localDataPath))
                        .asBitmap()
                        .into(target);
            } else if (!(dataURL == null || dataURL.isEmpty())) {
                Glide.with(context)
                        .load(dataURL)
                        .asBitmap()
                        .into(target);
            } else if (defaultIcon != null) {
                targetView.setIcon(
                        new IconicsDrawable(context).icon(defaultIcon));
            } else {
                targetView.setIcon(
                        new IconicsDrawable(context).icon(GoogleMaterial.Icon.gmd_broken_image));
            }
        }
    }
}
