package com.meishe.sdkdemo.capturescene

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.meishe.sdkdemo.R
import com.meishe.sdkdemo.capturescene.adapter.CaptureSceneAdapter
import com.meishe.sdkdemo.capturescene.data.CaptureSceneOnlineData
import com.meishe.sdkdemo.capturescene.data.CaptureSceneOnlineData.CaptureSceneDetails
import java.util.*


class BackgroundCaptureSceneFragment : Fragment()
     {

    private val FILENAME_SUFFIX = "capturescene"
    private lateinit var mContext: Context
    private var mCaptureSceneAdapter: CaptureSceneAdapter? = null
    private val mCaptureSceneDetails = LinkedList<CaptureSceneDetails>()
    private var mRecyclerView: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_background_capture_scene, container, false)
        initRecyclerView(view)
        initData()
        initListener()
        return view
    }

    private fun initRecyclerView(view: View?) {
        mRecyclerView = view?.findViewById(R.id.recyclerView)
        if (mCaptureSceneAdapter == null) {
            mCaptureSceneAdapter = CaptureSceneAdapter(mContext)
            mRecyclerView?.layoutManager =
                LinearLayoutManager(
                    mContext,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
            mRecyclerView?.adapter = mCaptureSceneAdapter
        }
        mCaptureSceneAdapter?.setDataList(mCaptureSceneDetails)
    }


    private fun initData() {
    }

    private fun initListener() {
        mCaptureSceneAdapter?.setItemClickListener { view, position ->

        }
    }


    companion object {
        @JvmStatic
        fun newInstance(): BackgroundCaptureSceneFragment {
            return BackgroundCaptureSceneFragment()
        }
    }

     fun update(t: LinkedList<CaptureSceneOnlineData.CaptureSceneDetails>?) {
        mCaptureSceneDetails.clear()
        t?.let { mCaptureSceneDetails.addAll(it) }
        if (mCaptureSceneDetails?.size>0){
            mCaptureSceneAdapter?.setDataList(mCaptureSceneDetails)
        }
    }
}