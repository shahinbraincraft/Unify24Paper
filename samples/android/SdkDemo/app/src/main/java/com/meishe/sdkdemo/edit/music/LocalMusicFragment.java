package com.meishe.sdkdemo.edit.music;

import androidx.fragment.app.Fragment;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.utils.dataInfo.MusicInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ms on 2018/7/13 0013.
 */

public class LocalMusicFragment extends Fragment {
    private View mView;
    private RecyclerView mMusicRv;
    private List<MusicInfo> mMusicData = new ArrayList<>( );
    private MusicListAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_local_music, container, false);
        mMusicRv = (RecyclerView) mView.findViewById(R.id.music_rv);

        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initMusicList( );
    }

    private void initMusicList() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity( ), LinearLayoutManager.VERTICAL, false);
        mMusicRv.setLayoutManager(linearLayoutManager);
        mAdapter = new MusicListAdapter(getActivity( ), mMusicData);
        mMusicRv.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new MusicListAdapter.OnItemClickListener( ) {
            @Override
            public void onItemClick(int position, MusicInfo audioInfo) {
                SelectMusicActivity parentActivity = (SelectMusicActivity) getActivity( );
                if (parentActivity != null) {
                    parentActivity.playMusic(audioInfo, true);
                }
            }
        });
    }

    public void loadAudioData(List<MusicInfo> medias) {
        if (medias == null || medias.isEmpty( )) {
            return;
        }
        mMusicData.clear( );
        mMusicData.addAll(medias);
        if (mAdapter != null) {
            mAdapter.updateData(mMusicData);
        }
    }

    public void clearPlayState() {
        if (mAdapter != null) {
            mAdapter.clearPlayState( );
        }
    }
}
