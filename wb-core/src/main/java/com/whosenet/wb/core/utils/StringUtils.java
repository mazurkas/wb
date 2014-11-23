package com.whosenet.wb.core.utils;

import java.util.UUID;

/**
 * Utils-String.
 *
 * @author <a href="http://ruo.whosenet.com">ruo.whosenet.com</>
 * @version 1.0
 */
public class StringUtils {

    public static String getUUI(){
        return UUID.randomUUID().toString().replace("-","");
    }

}
