package com.swjtu.huxin.accountmanagement.utils;

import android.content.Context;

import com.swjtu.huxin.accountmanagement.domain.AddItem;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by huxin on 2017/2/27.
 */

public class ItemXmlPullParserUtils {

    public static ArrayList<AddItem> parse(Context context,String XMLpath) throws Exception,IOException{
        InputStream in;
        try {
            in = context.getResources().getAssets().open(XMLpath);
        } catch (IOException e) {
            throw new IOException("xml file is not exist");
        }
        XmlPullParserFactory factory;
        try {
            factory = XmlPullParserFactory.newInstance();
            XmlPullParser xp = factory.newPullParser();
            xp.setInput(in, "UTF-8");
            int evtType = xp.getEventType();

            ArrayList<AddItem> AddItems = new ArrayList<AddItem>();
            AddItem AddItem = new AddItem();
            // 一直循环，直到文档结束
            while(evtType != XmlPullParser.END_DOCUMENT ) {
                switch (evtType) {
                    case XmlPullParser.START_TAG:
                        if ("root".equals(xp.getName())) {
                            AddItems = new ArrayList<AddItem>();
                        } else if ("item".equals(xp.getName())) {
                            AddItem = new AddItem();
                        } else if ("name".equals(xp.getName())) {
                            //获取当前节点的下一个节点的文本,并将指针移动到当前节点的结束节点上
                            String name = xp.nextText();
                            AddItem.setNameAddItem(name);
                        } else if ("icon".equals(xp.getName())) {
                            String icon = xp.nextText();
//                            int resID = context.getResources().getIdentifier(icon, "drawable", context.getPackageName());
                            AddItem.setIconAddItem(icon);
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if("item".equals(xp.getName())){
                            AddItems.add(AddItem);
                        }
                        break;
                    default:
                        break;
                }
                //获得下一个节点的信息
                evtType = xp.next();
            }
            in.close();
            return AddItems;
        } catch (Exception e) {
            throw new Exception("xml parse filed!");
        } finally {
            in.close();
        }
    }
}
