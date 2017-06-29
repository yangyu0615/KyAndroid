package com.ky.kyandroid.activity.evententry;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.Toast;

import com.ky.kyandroid.R;
import com.ky.kyandroid.adapter.ChildAdapter;
import com.ky.kyandroid.adapter.GroupAdapter;
import com.ky.kyandroid.bean.CodeValue;
import com.ky.kyandroid.db.dao.DescEntityDao;
import com.ky.kyandroid.entity.TFtSjEntity;
import com.ky.kyandroid.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

/**
 * Created by Caizhui on 2017-6-9.
 * 事件录入基本信息
 */

@SuppressLint("ValidFragment")
public class EventEntryAdd_Basic extends Fragment {


    /**
     * 事件名称
     */
    @BindView(R.id.thing_name_edt)
    EditText thingNameEdt;
    /**
     * 发生时间
     */
    @BindView(R.id.happen_time_edt)
    EditText happenTimeEdt;
    /**
     * 发生地点
     */
    @BindView(R.id.happen_address_edt)
    EditText happenAddressEdt;
    /**
     * 上访群体
     */
    @BindView(R.id.petition_groups_edt)
    EditText petitionGroupsEdt;
    /**
     * 到场部门LinearLayout
     */
    @BindView(R.id.field_departmen_layout)
    LinearLayout fieldDepartmenLayout;
    /**
     * 到场部门
     */
    @BindView(R.id.field_departmen_edt)
    EditText fieldDepartmenEdt;

    /**
     * 到场部门图标
     */
    @BindView(R.id.field_departmen_img)
    ImageView fieldDepartmenImg;
    /**
     * 表现形式
     */
    @BindView(R.id.pattern_manifestation_spinner)
    Spinner patternManifestationSpinner;

    /**
     * 现场态势
     */
    @BindView(R.id.field_morphology_spinner)
    Spinner fieldMorpholoySpinner;
    /**
     * 规模
     */
    @BindView(R.id.scope_text_spinner)
    Spinner scopeTextSpinner;
    /**
     * 涉及领域LinearLayout
     */
    @BindView(R.id.fields_involved_linearlayout)
    LinearLayout fieldsInvolvedLinearLayout;
    /**
     * 涉及领域
     */
    @BindView(R.id.fields_involved_edt)
    EditText fieldsInvolvedEdt;
    /**
     * 涉及领域img
     */
    @BindView(R.id.fields_involved_img)
    ImageView fieldsInvolvedImg;
    /**
     * 是否涉外
     */
    @BindView(R.id.foreign_related_spinner)
    Spinner foreignRelatedSpinner;
    /**
     * 是否涉疆
     */
    @BindView(R.id.involved_xinjiang_spinner)
    Spinner involvedXinjiangSpinner;
    /**
     * 是否涉舆情
     */
    @BindView(R.id.involve_public_opinion_spinner)
    Spinner involvePublicOpinionSpinner;
    /**
     * 是否公安处置
     */
    @BindView(R.id.public_security_disposal_spinner)
    Spinner publicSecurityDisposalSpinner;
    /**
     * 所属街道
     */
    @BindView(R.id.belong_street_edt)
    EditText belongStreetEdt;
    /**
     * 所属社区
     */
    @BindView(R.id.belong_community_spinner)
    Spinner belongCommunitySpinner;
    /**
     * 主要诉求
     */
    @BindView(R.id.main_appeals_edt)
    EditText mainAppealsEdt;
    /**
     * 事件概要
     */
    @BindView(R.id.event_summary_edt)
    EditText eventSummaryEdt;
    /**
     * >领导批示
     */
    @BindView(R.id.leadership_instructions_edt)
    EditText leadershipInstructionsEdt;


    /**
     * 设置Spinner控件的初始值
     */
    public List<CodeValue> spinnerList;

    /**
     * 数组 配置器 下拉菜单赋值用
     */
    ArrayAdapter<CodeValue> adapter;


    /**
     * 提示信息
     */
    private String message = "";

    private Intent intent;

    /**
     * type 0：新增 1：修改
     */
    public String type;

    public TFtSjEntity tFtSjEntity;


    public DescEntityDao descEntityDao;

    View showPupWindow = null; // 选择区域的view

    /** 一级菜单名称数组 **/
    String[] GroupNameArray = new String[]{};
    /** 二级菜单名称数组 **/
    String[] childNameArray  = new String[]{};

    ListView groupListView = null;
    ListView childListView = null;
    GroupAdapter groupAdapter = null;
    ChildAdapter childAdapter = null;

    TranslateAnimation animation;// 出现的动画效果
    // 屏幕的宽高
    public static int screen_width = 0;
    public static int screen_height = 0;

    private boolean[] tabStateArr = new boolean[4];// 标记tab的选中状态，方便设置

    PopupWindow mPopupWindow = null;

    @SuppressLint("ValidFragment")
    public EventEntryAdd_Basic(Intent intent) {
        this.intent = intent;
    }

    /**
     * 判断是那个树 dcbm(到场部门),sjly(涉及领域)
     */
    private  String spinnerType;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.evententeradd_basic_fragment, container, false);
        ButterKnife.bind(this, view);
        descEntityDao = new DescEntityDao();
        type = intent.getStringExtra("type");
        tFtSjEntity = (TFtSjEntity) intent.getSerializableExtra("tFtSjEntity");
        DisplayMetrics dm = new DisplayMetrics();
        EventEntryAdd_Basic.this.getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm); // 获取手机屏幕的大小
        screen_width = dm.widthPixels;
        screen_height = dm.heightPixels;
        initData();
        return view;
    }

    /**
     * 新增页面跟查看详情是同一个页面，初始化页面基本信息
     */
    public void initData() {
        spinnerList = descEntityDao.queryListForCV("sfsw");
        if (spinnerList == null) {
            //设置Spinner控件的初始值
            spinnerList = new ArrayList<CodeValue>();
        }
        //将可选内容与ArrayAdapter连接起来
        adapter = new ArrayAdapter<CodeValue>(EventEntryAdd_Basic.this.getActivity(), android.R.layout.simple_spinner_item, spinnerList);
        //设置下拉列表的风格
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        foreignRelatedSpinner.setAdapter(adapter);//将adapter 添加到spinner中
        involvedXinjiangSpinner.setAdapter(adapter);//将adapter 添加到spinner中
        involvePublicOpinionSpinner.setAdapter(adapter);//将adapter 添加到spinner中
        publicSecurityDisposalSpinner.setAdapter(adapter);//将adapter 添加到spinner中

        spinnerList = descEntityDao.queryListForCV("BXXS");
        if (spinnerList == null) {
            //设置Spinner控件的初始值
            spinnerList = new ArrayList<CodeValue>();
        }
        //将可选内容与ArrayAdapter连接起来
        adapter = new ArrayAdapter<CodeValue>(EventEntryAdd_Basic.this.getActivity(), android.R.layout.simple_spinner_item, spinnerList);
        //设置下拉列表的风格
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        patternManifestationSpinner.setAdapter(adapter);//将adapter 添加到表现形式spinner中

        spinnerList = descEntityDao.queryListForCV("XCTS");
        if (spinnerList == null) {
            //设置Spinner控件的初始值
            spinnerList = new ArrayList<CodeValue>();
        }
        //将可选内容与ArrayAdapter连接起来
        adapter = new ArrayAdapter<CodeValue>(EventEntryAdd_Basic.this.getActivity(), android.R.layout.simple_spinner_item, spinnerList);
        //设置下拉列表的风格
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fieldMorpholoySpinner.setAdapter(adapter);//将adapter 添加到现场态势spinner中

        spinnerList = descEntityDao.queryListForCV("sjgm");
        if (spinnerList == null) {
            //设置Spinner控件的初始值
            spinnerList = new ArrayList<CodeValue>();
        }
        //将可选内容与ArrayAdapter连接起来
        adapter = new ArrayAdapter<CodeValue>(EventEntryAdd_Basic.this.getActivity(), android.R.layout.simple_spinner_item, spinnerList);
        //设置下拉列表的风格
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        scopeTextSpinner.setAdapter(adapter);//将adapter 添加到规模spinner中


        spinnerList = new ArrayList<CodeValue>();
        spinnerList.add(new CodeValue("0", "社区1"));
        spinnerList.add(new CodeValue("1", "社区2"));


        //将可选内容与ArrayAdapter连接起来
        adapter = new ArrayAdapter<CodeValue>(EventEntryAdd_Basic.this.getActivity(), android.R.layout.simple_spinner_item, spinnerList);
        //设置下拉列表的风格
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        belongCommunitySpinner.setAdapter(adapter);//将adapter 添加到所属社区spinner中
        if (tFtSjEntity != null) {
            //当状态等于1的时候，表示为草稿，可以修改，其他的时候只能查看信息
            if (!"0".equals(tFtSjEntity.getZt()) && !"3".equals(tFtSjEntity.getZt())) {
                thingNameEdt.setEnabled(false);
                happenTimeEdt.setEnabled(false);
                happenAddressEdt.setEnabled(false);
                petitionGroupsEdt.setEnabled(false);
                //fieldDepartmenEdt.setEnabled(false);
                fieldsInvolvedEdt.setEnabled(false);
                belongStreetEdt.setEnabled(false);
                mainAppealsEdt.setEnabled(false);
                eventSummaryEdt.setEnabled(false);
                leadershipInstructionsEdt.setEnabled(false);
                //以下为下拉框控件
                patternManifestationSpinner.setEnabled(false);
                fieldMorpholoySpinner.setEnabled(false);
                scopeTextSpinner.setEnabled(false);
                foreignRelatedSpinner.setEnabled(false);
                involvedXinjiangSpinner.setEnabled(false);
                involvePublicOpinionSpinner.setEnabled(false);
                publicSecurityDisposalSpinner.setEnabled(false);
                belongCommunitySpinner.setEnabled(false);
            }
            thingNameEdt.setText(tFtSjEntity.getSjmc());
            happenTimeEdt.setText(tFtSjEntity.getFssj());
            happenAddressEdt.setText(tFtSjEntity.getFsdd());
            petitionGroupsEdt.setText(tFtSjEntity.getSfsqqt());
            //fieldDepartmenEdt.setText(tFtSjEntity.getDcbm());
            fieldsInvolvedEdt.setText(tFtSjEntity.getSjly());
            belongStreetEdt.setText(tFtSjEntity.getSsjd());
            mainAppealsEdt.setText(tFtSjEntity.getZysq());
            eventSummaryEdt.setText(tFtSjEntity.getSjgyqk());
            leadershipInstructionsEdt.setText(tFtSjEntity.getLdps());
            //以下为下拉控件设置默认值
            if (tFtSjEntity.getBxxs() != null) {
                patternManifestationSpinner.setSelection(Integer.valueOf(tFtSjEntity.getBxxs().split(",")[0]) - 1);
            }
            if (tFtSjEntity.getXcts() != null) {
                fieldMorpholoySpinner.setSelection(Integer.valueOf(tFtSjEntity.getXcts()));
            }
            if (tFtSjEntity.getGm() != null) {
                scopeTextSpinner.setSelection(Integer.valueOf(tFtSjEntity.getGm()));
            }
            if (tFtSjEntity.getSfsw() != null) {
                foreignRelatedSpinner.setSelection(Integer.valueOf(tFtSjEntity.getSfsw()));
            }
            if (tFtSjEntity.getSfsj() != null) {
                involvedXinjiangSpinner.setSelection(Integer.valueOf(tFtSjEntity.getSfsj()));
            }
            if (tFtSjEntity.getSfsyq() != null) {
                involvePublicOpinionSpinner.setSelection(Integer.valueOf(tFtSjEntity.getSfsyq()));
            }
            if (tFtSjEntity.getSfgacz() != null) {
                publicSecurityDisposalSpinner.setSelection(Integer.valueOf(tFtSjEntity.getSfgacz()));
            }

        }

    }

    @OnTouch({R.id.happen_time_edt})
    public boolean OnTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            /** 点击发生时间控件 **/
            case R.id.happen_time_edt:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    happenTimeEdt.clearFocus();
                    Calendar c = Calendar.getInstance();
                    new DatePickerDialog(EventEntryAdd_Basic.this.getActivity(), new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            Date date = new Date(System.currentTimeMillis());
                            SimpleDateFormat dateFormat = new SimpleDateFormat(" HH:mm:ss");
                            String time = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                            time += dateFormat.format(date);
                            happenTimeEdt.setText(time);
                        }
                    }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
                    return false;
                }
                break;
        }
        return false;
    }

    /**
     * 封装数据
     */
    public TFtSjEntity PackageData() {
        //每次保存时先清空message
        message = "";
        if (tFtSjEntity == null) {
            tFtSjEntity = new TFtSjEntity();
        }
        String thingNameString = thingNameEdt.getText().toString();
        String happenTimeString = happenTimeEdt.getText().toString();
        String happenAddressString = happenAddressEdt.getText().toString();
        String petitionGroupsString = petitionGroupsEdt.getText().toString();
        // String fieldDepartmenString = descEntityDao.queryCodeByName("dcbm", fieldDepartmenEdt.getText().toString());
        String fieldDepartmenString =  fieldDepartmenEdt.getText().toString();
        String patternManifestationString = descEntityDao.queryCodeByName("BXXS", patternManifestationSpinner.getSelectedItem().toString());
        String fieldMorpholoySpinnerString = descEntityDao.queryCodeByName("XCTS", fieldMorpholoySpinner.getSelectedItem().toString());
        String scopeTextString = descEntityDao.queryCodeByName("sjgm", scopeTextSpinner.getSelectedItem().toString());
        String fieldsInvolved = descEntityDao.queryCodeByName("sjly", fieldsInvolvedEdt.getText().toString());
        String foreignRelatedString = descEntityDao.queryCodeByName("sfsw", foreignRelatedSpinner.getSelectedItem().toString());
        String involvedXinjiangString = descEntityDao.queryCodeByName("sfsw", involvedXinjiangSpinner.getSelectedItem().toString());
        String involvePublicOpinionString = descEntityDao.queryCodeByName("sfsw", involvePublicOpinionSpinner.getSelectedItem().toString());
        String publicSecurityDisposalString = descEntityDao.queryCodeByName("sfsw", publicSecurityDisposalSpinner.getSelectedItem().toString());
        String belongStreetString = belongStreetEdt.getText().toString();
        String belongCommunityString = belongCommunitySpinner.getSelectedItem().toString();
        String mainAppealsString = mainAppealsEdt.getText().toString();
        String eventSummaryString = eventSummaryEdt.getText().toString();
        String leadershipInstructionsString = leadershipInstructionsEdt.getText().toString();
        if (StringUtils.isBlank(thingNameString)) {
            message += "事件名称不能为空\n";
        } else {
            tFtSjEntity.setSjmc(thingNameString);
        }
        if (StringUtils.isBlank(happenTimeString)) {
            message += "发生时间不能为空\n";
        } else {
            tFtSjEntity.setFssj(happenTimeString);
        }
        if (StringUtils.isBlank(happenAddressString)) {
            message += "发生地点不能为空\n";
        } else {
            tFtSjEntity.setFsdd(happenAddressString);
        }
        tFtSjEntity.setSfsqqt(petitionGroupsString);
       if (StringUtils.isBlank(fieldDepartmenString)) {
            message += "到场部门不能为空\n";
        } else {
            tFtSjEntity.setDcbm(fieldDepartmenString);
        }
        if (StringUtils.isBlank(patternManifestationString)) {
            message += "表现形式不能为空\n";
        } else {
            tFtSjEntity.setBxxs(patternManifestationString);
        }
        if (StringUtils.isBlank(fieldMorpholoySpinnerString)) {
            message += "现场态势不能为空\n";
        } else {
            tFtSjEntity.setXcts(fieldMorpholoySpinnerString);
        }
        if (StringUtils.isBlank(scopeTextString)) {
            message += "规模不能为空\n";
        } else {
            tFtSjEntity.setGm(scopeTextString);
        }
        if (StringUtils.isBlank(fieldsInvolved)) {
            message += "涉及领域不能为空\n";
        } else {
            tFtSjEntity.setSjly(fieldsInvolved);
        }
        tFtSjEntity.setSfsw(foreignRelatedString);
        tFtSjEntity.setSfsj(involvedXinjiangString);
        tFtSjEntity.setSfsyq(involvePublicOpinionString);
        tFtSjEntity.setSfgacz(publicSecurityDisposalString);
        tFtSjEntity.setSsjd(belongStreetString);
        tFtSjEntity.setSssq(belongCommunityString);
        if (StringUtils.isBlank(mainAppealsString)) {
            message += "主要诉求不能为空\n";
        } else {
            tFtSjEntity.setZysq(mainAppealsString);
        }
        if (StringUtils.isBlank(eventSummaryString)) {
            message += "事件概要不能为空\n";
        } else {
            tFtSjEntity.setSjgyqk(eventSummaryString);
        }
        tFtSjEntity.setLdps(leadershipInstructionsString);
        if (!"".equals(message)) {
            Toast.makeText(EventEntryAdd_Basic.this.getActivity(), message, Toast.LENGTH_SHORT).show();
        } else {
            return tFtSjEntity;
        }
        return null;
    }

    @OnClick({R.id.field_departmen_layout,R.id.fields_involved_linearlayout})
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.field_departmen_layout:
               /* tabStateArr[0] = !tabStateArr[0];
                int[] location = new int[2];
                spinnerType="sjly";
                fieldDepartmenLayout.getLocationOnScreen(location);// 获取控件在屏幕中的位置,方便展示Popupwindow
                animation=null;
                animation = new TranslateAnimation(0, 0, -700, location[1]);
                animation.setDuration(500);
                List<CodeValue> codeValueList = descEntityDao.queryPidList();
                if(codeValueList!=null && codeValueList.size()>0){
                    *//** 一级菜单名称数组 **//*
                    GroupNameArray = new String[codeValueList.size()];
                    for(int i=0;i<codeValueList.size();i++){
                        GroupNameArray[i]= codeValueList.get(i).getValue();
                    }
                }
                if (tabStateArr[0]) {// 判断是否需要关闭弹出层
                    showPupupWindow();
                } else {
                    mPopupWindow.dismiss();
                }*/
                break;
            case R.id.fields_involved_linearlayout:
                tabStateArr[1] = !tabStateArr[1];
                int[] location1 = new int[2];
                fieldsInvolvedLinearLayout.getLocationOnScreen(location1);// 获取控件在屏幕中的位置,方便展示Popupwindow
                animation=null;
                spinnerType="sjly";
                animation = new TranslateAnimation(0, 0, -700, location1[1]);
                animation.setDuration(500);
                List<CodeValue> codeValueList1 = descEntityDao.queryPidList();
                if(codeValueList1!=null && codeValueList1.size()>0){
                    /** 一级菜单名称数组 **/
                    GroupNameArray = new String[codeValueList1.size()];
                    for(int i=0;i<codeValueList1.size();i++){
                        GroupNameArray[i]= codeValueList1.get(i).getValue();
                    }
                }
                if (tabStateArr[1]) {// 判断是否需要关闭弹出层
                    showPupupWindow();
                } else {
                    mPopupWindow.dismiss();
                }
                break;
        }

    }

  /*  *//**
            * 设置tab的状态
	 *
             * @param img
	 *            // ImageView对象
	 *            // TextView对象
             * @param state
	 *            // 状态
      *//*
    private void setTabState(ImageView img, boolean state) {
        if (state) {// 选中状态
            img.setBackgroundResource(R.mipmap.up);
        } else {
            img.setBackgroundResource(R.mipmap.down);
        }
    }*/

    /**
     * 初始化 PopupWindow
     *
     * @param view
     */
    public void initPopuWindow(View view) {
		/* 第一个参数弹出显示view 后两个是窗口大小 */
        mPopupWindow = new PopupWindow(view, screen_width, screen_height);
		/* 设置背景显示 */
        mPopupWindow.setBackgroundDrawable(getResources().getDrawable(
                R.drawable.mypop_bg));
		/* 设置触摸外面时消失 */
         mPopupWindow.setOutsideTouchable(true);

        mPopupWindow.update();
        mPopupWindow.setTouchable(true);
		/* 设置点击menu以外其他地方以及返回键退出 */
        mPopupWindow.setFocusable(true);

        /**
         * 1.解决再次点击MENU键无反应问题 2.sub_view是PopupWindow的子View
         */
        view.setFocusableInTouchMode(true);
    }

    /**
     * 展示区域选择的对话框
     */
    private void showPupupWindow() {
        if (mPopupWindow == null) {
            showPupWindow = LayoutInflater.from(EventEntryAdd_Basic.this.getActivity()).inflate(
                    R.layout.bottom_layout, null);
            initPopuWindow(showPupWindow);

            groupListView = (ListView) showPupWindow
                    .findViewById(R.id.listView1);
            childListView = (ListView) showPupWindow
                    .findViewById(R.id.listView2);

            groupAdapter = new GroupAdapter(EventEntryAdd_Basic.this.getActivity(), GroupNameArray);
            groupListView.setAdapter(groupAdapter);
        }

        groupListView.setOnItemClickListener(new MyItemClick());

        childListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String name=(String) parent.getItemAtPosition(position);
                    Toast.makeText(EventEntryAdd_Basic.this.getActivity(),position+"",Toast.LENGTH_SHORT).show();
                if("sjly".equals(spinnerType)){
                    fieldsInvolvedEdt.setText(name);
                    fieldsInvolvedImg.setBackgroundResource(R.mipmap.down);
                }else{
                    fieldDepartmenEdt.setText(name);
                    fieldDepartmenImg.setBackgroundResource(R.mipmap.down);
                }
                mPopupWindow.dismiss();
            }
        });

        showPupWindow.setAnimation(animation);
        showPupWindow.startAnimation(animation);

        if("sjly".equals(spinnerType)){
            mPopupWindow.showAsDropDown(fieldsInvolvedLinearLayout, -5, 10);
        }else{
            mPopupWindow.showAsDropDown(fieldDepartmenLayout, -5, 10);
        }


    }

    class MyItemClick implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {

            groupAdapter.setSelectedPosition(position);
            String pidName = (String) groupAdapter.getItem(position);
            String pidCode = descEntityDao.queryCodeByName(spinnerType,pidName);
            List<CodeValue>  childCodeValueList= descEntityDao.queryValueListByPid(spinnerType,pidCode);
            if(childCodeValueList!=null && childCodeValueList.size()>0){
                childNameArray = new  String[childCodeValueList.size()];
                for(int i=0;i<childCodeValueList.size();i++){
                    childNameArray[i]=childCodeValueList.get(i).getValue();
                }
            }
            if (childAdapter == null) {
                childAdapter = new ChildAdapter(EventEntryAdd_Basic.this.getActivity());
                childListView.setAdapter(childAdapter);
            }

            Message msg = new Message();
            msg.what = 20;
            msg.arg1 = position;
            handler.sendMessage(msg);
        }

    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 20:
                    childAdapter.setChildData(childNameArray);
                    childAdapter.notifyDataSetChanged();
                    groupAdapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }

        };
    };

}
