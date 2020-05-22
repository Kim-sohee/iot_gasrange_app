package org.techtown.mycheck;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SliderAdapter extends PagerAdapter {

    Context context;
    LayoutInflater inflater;

    public  int[] imagesArray={R.drawable.ic_sentiment,R.drawable.setting1,R.drawable.setting2,R.drawable.setting3,R.drawable.setting4};
    public String[] titleArray={"반갑습니다","전원버튼","카메라 버튼","가스벨브 조절","타이머 설정"};
    public String[] descriptionArray={"키친봇을 사용해 주셔서 감사합니다. 키친봇 앱 사용설명을 시작합니다."
            ,"전원버튼을 누르면 카메라가 켜지고 화면으로 주방상황을 볼 수 있습니다."
            ,"화구버튼을 눌러 카메라의 위치를 선택할 수 있습니다."
            ,"가스밸브를 조절하여 불의 세기를 조절할 수 있습니다. 가운데 숫자 버튼을 눌러 2개의 화구를 조절하세요."
            ,"막대 그래프를 조절하여 원하는 시간을 지정할 수 있습니다.(단, 최대 시간은 1시간 입니다.) 시간을 지정하였으면 버튼을 눌러 타이머를 실행하세요."};

    public int[] backgroundColorArray={Color.rgb(68,68,68),
                            Color.rgb(204,102,102),Color.rgb(51,153,153)
                            ,Color.rgb(102,153,204),Color.rgb(120,120,204)};

    public SliderAdapter(Context context){
        this.context=context;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return (view==o);
    }

    @Override
    public int getCount() {
        return titleArray.length;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout)object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        inflater=(LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view=inflater.inflate(R.layout.slide_setting,container,false);

        LinearLayout linearLayout=(LinearLayout)view.findViewById(R.id.linear1);
        ImageView imageView=(ImageView)view.findViewById(R.id.sildeimage);
        TextView text_title=(TextView)view.findViewById(R.id.title);
        TextView text_description=(TextView)view.findViewById(R.id.description);

        linearLayout.setBackgroundColor(backgroundColorArray[position]);
        imageView.setImageResource(imagesArray[position]);
        text_title.setText(titleArray[position]);
        text_description.setText(descriptionArray[position]);
        container.addView(view);

        return view;
    }
}
