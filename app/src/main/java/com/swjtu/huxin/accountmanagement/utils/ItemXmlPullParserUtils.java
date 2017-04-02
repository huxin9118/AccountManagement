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

    public static ArrayList<AddItem> parseAddItemList(Context context,String XMLpath) throws Exception,IOException{
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
            AddItem addItem = new AddItem();
            // 一直循环，直到文档结束
            while(evtType != XmlPullParser.END_DOCUMENT ) {
                switch (evtType) {
                    case XmlPullParser.START_TAG:
                        if ("root".equals(xp.getName())) {
                            AddItems = new ArrayList<AddItem>();
                        } else if ("item".equals(xp.getName())) {
                            addItem = new AddItem();
                        } else if ("name".equals(xp.getName())) {
                            //获取当前节点的下一个节点的文本,并将指针移动到当前节点的结束节点上
                            String name = xp.nextText();
                            addItem.setNameAddItem(name);
                        } else if ("icon".equals(xp.getName())) {
                            String icon = xp.nextText();
//                            int resID = context.getResources().getIdentifier(icon, "drawable", context.getPackageName());
                            addItem.setIconAddItem(icon);
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if("item".equals(xp.getName())){
                            AddItems.add(addItem);
                        }
                        break;
                    default:
                        break;
                }
                //获得下一个节点的信息
                evtType = xp.next();
            }
            return AddItems;
        } catch (Exception e) {
            throw new Exception("xml parse filed!");
        } finally {
            in.close();
        }
    }

    public static String parseIconColor(Context context,String XMLpath,String icon) throws Exception,IOException{
        InputStream in;
        try {
            in = context.getResources().getAssets().open(XMLpath);
        } catch (IOException e) {
            throw new IOException("xml file is not exist");
        }
        XmlPullParserFactory factory;
        boolean isParse = false;
        try {
            factory = XmlPullParserFactory.newInstance();
            XmlPullParser xp = factory.newPullParser();
            xp.setInput(in, "UTF-8");
            int evtType = xp.getEventType();
            // 一直循环，直到文档结束
            while(evtType != XmlPullParser.END_DOCUMENT ) {
                switch (evtType) {
                    case XmlPullParser.START_TAG:
                        if ("icon".equals(xp.getName()) && icon.equals(xp.nextText())) {
                            isParse = true;
                        }
                        if("color".equals(xp.getName()) && isParse){
                            return xp.nextText();
                        }
                        break;
                    default:
                        break;
                }
                //获得下一个节点的信息
                evtType = xp.next();
            }
            throw new Exception("xml parse filed!");
        } catch (Exception e) {
            throw new Exception("xml parse filed!");
        } finally {
            in.close();
        }
    }
}
