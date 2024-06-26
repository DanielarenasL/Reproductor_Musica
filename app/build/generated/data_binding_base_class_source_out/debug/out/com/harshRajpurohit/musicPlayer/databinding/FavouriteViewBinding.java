// Generated by view binder compiler. Do not edit!
package com.harshRajpurohit.musicPlayer.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;
import com.harshRajpurohit.musicPlayer.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class FavouriteViewBinding implements ViewBinding {
  @NonNull
  private final MaterialCardView rootView;

  @NonNull
  public final ShapeableImageView songImgFV;

  @NonNull
  public final TextView songNameFV;

  private FavouriteViewBinding(@NonNull MaterialCardView rootView,
      @NonNull ShapeableImageView songImgFV, @NonNull TextView songNameFV) {
    this.rootView = rootView;
    this.songImgFV = songImgFV;
    this.songNameFV = songNameFV;
  }

  @Override
  @NonNull
  public MaterialCardView getRoot() {
    return rootView;
  }

  @NonNull
  public static FavouriteViewBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static FavouriteViewBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.favourite_view, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static FavouriteViewBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.songImgFV;
      ShapeableImageView songImgFV = ViewBindings.findChildViewById(rootView, id);
      if (songImgFV == null) {
        break missingId;
      }

      id = R.id.songNameFV;
      TextView songNameFV = ViewBindings.findChildViewById(rootView, id);
      if (songNameFV == null) {
        break missingId;
      }

      return new FavouriteViewBinding((MaterialCardView) rootView, songImgFV, songNameFV);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
