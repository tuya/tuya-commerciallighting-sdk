package com.thingclips.smart.commercial.lighting.demo.pages.area.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.thingclips.smart.android.demo.R;
import com.thingclips.smart.commercial.lighting.demo.bean.GroupPackBeanWrapper;
import com.thingclips.smart.commercial.lighting.demo.pages.adapter.GroupListAdapter;
import com.thingclips.smart.commercial.lighting.demo.pages.area.presenter.GroupListPresenter;
import com.thingclips.smart.commercial.lighting.demo.widget.WrapContentLinearLayoutManager;
import com.thingclips.smart.uispecs.component.ProgressUtils;
import com.thingclips.smart.uispecs.component.dialog.BooleanConfirmAndCancelListener;
import com.thingclips.smart.uispecs.component.util.FamilyDialogUtils;
import com.thingclips.smart.utils.ToastUtil;
import com.thingclips.stencil.base.fragment.BaseFragment;

import java.util.List;

public class GroupListFragment extends BaseFragment implements IGroupListView {
    private RecyclerView recyclerView;
    private View emptyView;
    private GroupListAdapter groupListAdapter;
    private final GroupListPresenter grouListPresenter = new GroupListPresenter(this);

    @Override
    public String getPageName() {
        return TAG;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.cl_recycler_view, container, false);
        recyclerView = rootView.findViewById(R.id.rv_recycler_view);
        emptyView = rootView.findViewById(R.id.emptyView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initAdapter();
        initRecyclerView();
        initData();
        grouListPresenter.refreshGroupList(false);
    }

    private void initData() {
        grouListPresenter.setBundle(getArguments());
    }

    private void initRecyclerView() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                    if (layoutManager instanceof LinearLayoutManager) {
                        int lastVisbileItem = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
                        if (grouListPresenter != null) {
                            if (lastVisbileItem == groupListAdapter.getItemCount() - 1) {
                                //last item is visible,and load more data
                                grouListPresenter.loadMoreData();
                            }
                        }
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    private void initAdapter() {
        groupListAdapter = new GroupListAdapter(getActivity());
        groupListAdapter.setClickListener(new GroupListAdapter.GroupClickListener() {
            @Override
            public void onGroupClick(GroupPackBeanWrapper groupBean) {
            }

            @Override
            public void turnOn(GroupPackBeanWrapper groupBean) {
                grouListPresenter.turnOn(groupBean);
            }

            @Override
            public void turnOff(GroupPackBeanWrapper groupBean) {
                grouListPresenter.turnOff(groupBean);
            }
        });
        groupListAdapter.setChangeListener(size -> {
            if (size <= 0) {
                emptyView.setVisibility(View.VISIBLE);
            }
        });
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(getActivity()));
        recyclerView.setAdapter(groupListAdapter);
    }

    @Override
    public void refreshGroupItem(GroupPackBeanWrapper groupInfo) {
        groupListAdapter.refreshGroupDevCount(groupInfo);
    }

    @Override
    public void loadMoreGroupList(List<GroupPackBeanWrapper> result) {
        groupListAdapter.addData(result);
        emptyView.setVisibility(View.GONE);
    }

    public static final String TAG = "GroupListFragment";

    public static GroupListFragment newInstance() {
        return new GroupListFragment();
    }

    @Override
    public void toast(String msg) {
        if (getActivity() == null) {
            return;
        }
        ToastUtil.showToast(getActivity(), msg);
    }

    @Override
    public void toast(int resId) {
        if (getActivity() == null) {
            return;
        }
        ToastUtil.showToast(getActivity(), resId);
    }

    @Override
    public void showEmptyView() {
        groupListAdapter.clearData();
        groupListAdapter.setNotifyDataSetChanged();
    }

    @Override
    public void clearGroupList() {
        groupListAdapter.clearData();
    }

    @Override
    public void refreshGroupName(String groupPackId, String newName) {
        groupListAdapter.refreshGroupName(groupPackId, newName);
    }

    @Override
    public void addGroup(GroupPackBeanWrapper groupBean) {
        groupListAdapter.addData(groupBean);
    }

    @Override
    public void hideProgress() {
        ProgressUtils.hideLoadingViewInPage();
    }

    @Override
    public void removeGroup(String groupId) {
        groupListAdapter.removeData(groupId);
    }

    @Override
    public void showProgress() {
        ProgressUtils.showLoadViewInPage(getActivity());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        grouListPresenter.onDestroy();
    }

    @Override
    public void showEmptyGroupDialog(GroupPackBeanWrapper groupBean, boolean dismissGroup) {
        if (getActivity() == null || requireActivity().isFinishing() || getContext() == null) {
            return;
        }
        String content = getActivity().getString(R.string.group_no_device);
        FamilyDialogUtils.showConfirmAndCancelDialog(getActivity(),
                dismissGroup ? requireActivity().getResources().getString(R.string.group_dismiss) : "",
                content,
                dismissGroup ? requireActivity().getResources().getString(R.string.group_dismiss) : requireActivity().getResources().getString(R.string.thing_confirm),
                dismissGroup ? requireContext().getResources().getString(R.string.thing_cancel) : "", true,
                new BooleanConfirmAndCancelListener() {
                    @Override
                    public boolean onCancel(Object o) {
                        return true;
                    }

                    @Override
                    public boolean onConfirm(Object o) {
                        if (dismissGroup) {
                            grouListPresenter.dismissGroupPack(groupBean.getGroupPackBean().getGroupPackageId());
                        }
                        return true;
                    }
                });
    }

    @Override
    public void showGroupPackControlDialog(GroupPackBeanWrapper areaBeanWrapper) {
        if (getActivity() == null || requireActivity().isFinishing()) {
            return;
        }
//        AreaDpControlDialog().newInstance(activity, areaBeanWrapper)
//                .show((activity as AppCompatActivity).supportFragmentManager, "");
    }
}


