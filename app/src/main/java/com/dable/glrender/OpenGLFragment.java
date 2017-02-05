package com.dable.glrender;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dable.R;

public class OpenGLFragment extends Fragment {
    //private GlSurfaceViewClass mGLView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

//        if (container == null) {
//            return null;
//        }
        View view = inflater.inflate(R.layout.gatt_services_characteristics, container, false);
//        mGLView = (GlSurfaceViewClass) view.findViewById(R.id.gl_surface_view);
//        mGLView.setEGLContextClientVersion(2);
//        RendererClass rendererclass = new RendererClass(getActivity());
//        mGLView.setRenderer(rendererclass);
//        mGLView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        return view;
    }
}