/*
 * $Id: $
 */
package effect;

import java.util.Date;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.time.DateFormatUtils;

/**
 * $Id: $
 */
public class EAUtils
{
    public static String now()
    {
        return DateFormatUtils.ISO_DATETIME_FORMAT.format(new Date());
    }

    public static String now(String format)
    {
        return DateFormatUtils.format(new Date(), format);
    }
    
    public static String getExtension(String s)
    {
        return FilenameUtils.getExtension(s);
    }

}
