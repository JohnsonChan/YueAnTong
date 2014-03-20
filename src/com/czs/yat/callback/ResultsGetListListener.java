/**
 * Title: ucweb
 * Description: 
 * Copyright: Copyright (c) 2010
 * Company: ucweb.com
 * @author chenzs@ucweb.com
 * @version 1.0
 * Date:2014-3-20   Time:上午8:57:31
 */
package com.czs.yat.callback;

import java.util.ArrayList;

import com.czs.yat.data.Result;

public interface ResultsGetListListener {
    void refreshResults(ArrayList<Result> resultList);
}
