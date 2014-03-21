/**
 * Title: ucweb Description: Copyright: Copyright (c) 2010 Company: ucweb.com
 * 
 * @author chenzs@ucweb.com
 * @version 1.0 Date:2014-3-21 Time:下午7:28:24
 */
package com.czs.yat.net;

import com.czs.yat.data.SearchType;

/**
 * 实现功能：
 */
public class NetHelper {
    public final static String HOST = "http://218.192.99.29:8080";
    
    public static  String getUrl(SearchType searchType)
    {
        String url = null;
        switch (searchType) {
            case CHEM:  
                url = "/yat/yatChem.jsp";
                break;
            case LAW:
                url = "/yat/yatLaw.jsp";
                break;
            case STANDARD:
                url = "/yat/yatChem.jsp";
                break;
            case OPERATION:
                url = "/yat/yatChem.jsp";
                break;
            case LAW_BASIS:
                url = "/yat/yatChem.jsp";
                break;
            case EQUIPMENT:
                url = "/yat/yatChem.jsp";
                break;
            case LICENSING:
                url = "/yat/yatChem.jsp";
                break;
            case CONTACTS:
                url = "yat/yatContacts.jsp";
                break;
            default:
                break;
        } 
        return url;
    }
    
    public static  String getQueryUrl(SearchType searchType)
    {
        String url = null;
        switch (searchType) {
            case CHEM:  
                url = "/yat/yatChemQuery.jsp";
                break;
            case LAW:
                url = "/yat/yatLawQuery.jsp";
                break;
            case STANDARD:
                url = "/yat/yatChem.jsp";
                break;
            case OPERATION:
                url = "/yat/yatChem.jsp";
                break;
            case LAW_BASIS:
                url = "/yat/yatChem.jsp";
                break;
            case EQUIPMENT:
                url = "/yat/yatChem.jsp";
                break;
            case LICENSING:
                url = "/yat/yatChem.jsp";
                break;
            case CONTACTS:
                url = "/yat/yatConQuery.jsp";
                break;
            default:
                break;
        } 
        return url;
    }
}
