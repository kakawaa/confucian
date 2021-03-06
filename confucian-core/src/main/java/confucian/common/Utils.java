package confucian.common;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.io.CharStreams;

import org.apache.commons.io.input.BOMInputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestResult;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 通用辅助操作
 */
public interface Utils {

    Logger LOGGER = LogManager.getLogger();

    /**
     * 字符串切割,实现字符串自动换行
     *
     * @param text     the text
     * @param maxWidth the max width
     * @param ft       the ft
     *
     * @return the string [ ]
     */
    static List<String> format(String text, int maxWidth, Font ft) {
        List<String> strings = Lists.newArrayList();
        int width = maxWidth - ft.getSize() * 2;
        Graphics2D g = new BufferedImage(maxWidth, ft.getSize(),
                BufferedImage.TYPE_INT_ARGB).createGraphics();
        g.setFont(ft);
        FontMetrics m = g.getFontMetrics();
        if (m.stringWidth(text) < width) {
            strings.add(text);
            g.dispose();
        } else {
            int strWidth = 0;
            int fromIndex = 0;
            char[] chars = text.toCharArray();
            for (int i = 0; i < text.length(); ) {
                strWidth += m.charWidth(chars[i]);
                if (chars[i] == '\n' || strWidth > width) {
                    strings.add(text.substring(fromIndex, i));
                    strWidth = 0;
                    fromIndex = i;
                }
                i++;
            }
            if (fromIndex < text.length()) // 加上最后一行
                strings.add(text.substring(fromIndex, text.length()));
            g.dispose();
        }
        return strings;
    }

    /**
     * 从字符串中获取double的列表(带符号)
     *
     * @param text 字符串
     *
     * @return 整数列表 integer list from string
     */
    static List<Double> getDoubleListFromString(String text) {
        List<Double> integerList = Lists.newArrayList();
        Matcher matcher = Pattern.compile("(-)?[1-9]\\d*\\.\\d*|(-)?0\\.\\d*[1-9]\\d*").matcher(text);

        while (matcher.find()) integerList.add(Double.valueOf(matcher.group()));
        return integerList;
    }

    /**
     * 获取文件的路径字符串.
     *
     * @param fileName 文件名
     *
     * @return 字符串 file path for upload
     */
    static String getFilePathForUpload(String fileName) {
        LOGGER.debug("文件名:" + fileName);
        return System.getProperty("user.dir") + File.separator + fileName;
    }

    /**
     * 获取完整的方法名称.
     *
     * @param m 方法
     *
     * @return 完整的方法名 full method name
     */
    static String getFullMethodName(Method m) {
        return Joiner.on(".").join(m.getDeclaringClass().getName(), m.getName());
    }

    /**
     * 获取ID，用于自定义报表
     *
     * @param result result
     *
     * @return id
     */
    static int getId(ITestResult result) {
        int id = result.getTestClass().getName().hashCode();
        id = 31 * id + result.getMethod().getMethodName().hashCode();
        id = 31 * id + result.getMethod().getInstance().hashCode();
        id = 31 * id + (result.getParameters() != null ?
                Arrays.hashCode(result.getParameters()) :
                0);
        return id;
    }

    /**
     * 从字符串中获取整数的列表(带符号)
     *
     * @param text 字符串
     *
     * @return 整数列表 integer list from string
     */
    static List<Integer> getIntegerListFromString(String text) {
        List<Integer> integerList = Lists.newArrayList();
        Matcher matcher = Pattern.compile("(-)?\\d+").matcher(text);

        while (matcher.find()) integerList.add(Integer.valueOf(matcher.group()));
        return integerList;
    }

    /**
     * 从字符串中获取字母的列表
     *
     * @param text 字符串
     *
     * @return letter list from string
     */
    static List<String> getLetterListFromString(String text) {
        List<String> list = Lists.newArrayList();
        Matcher matcher = Pattern.compile("[A-Za-z]+").matcher(text);

        while (matcher.find()) list.add(matcher.group());
        return list;
    }

    /**
     * 从字符串中获取金额
     *
     * @param text the text
     *
     * @return the double
     */
    static double getMoneyFromString(String text) {
        List<Double> doubleList = getMoneyListFromString(text);
        return doubleList.size() == 0 ? 0.0 : doubleList.get(0);
    }

    /**
     * 从字符串中获取金额列表
     *
     * @param text the text
     *
     * @return the double List
     */
    static List<Double> getMoneyListFromString(String text) {
        List<Double> list = Lists.newArrayList();
        Matcher matcher = Pattern.compile("((-)?[1-9]\\d{0,10})(,[0-9]{3})*(\\.?\\d+)?|((-)?[1-9]\\d*|0)(\\.\\d+)?")
                .matcher(text);

        while (matcher.find()) list.add(Double.valueOf(matcher.group().trim().replace(",", "")));
        return list;
    }

    /**
     * Creates a reader for a resource in the relative path
     *
     * @param relativePath relative path of the resource to be read
     *
     * @return a reader of the resource
     */
    static Reader getReader(String relativePath) {
        return new InputStreamReader(new BOMInputStream(Thread.currentThread().getContextClassLoader().getResourceAsStream(relativePath)), Charsets.UTF_8);
    }

    /**
     * 返回资源文件的完整路径，
     * 如果找不到文件将返回null。
     *
     * @param fileName 文件名
     *
     * @return 资源文件夹路径 resources
     */
    static String getResources(String fileName) {
        LOGGER.debug("文件名:" + fileName);
        String returnFilePath = null;

        try {
            URL resource = Thread.currentThread().getContextClassLoader().getResource(fileName);
            if (resource != null) switch (OSName.get()) {
                case UNIX:
                    returnFilePath = resource.getPath();
                    break;
                case WIN:
                    returnFilePath = resource.getPath().substring(1).replace("%20", " ");
                    break;
                case MAC:
                    returnFilePath = resource.getPath();
                    break;
                default:
                    break;
            }
            if (returnFilePath != null)
                returnFilePath = java.net.URLDecoder.decode(returnFilePath, "utf-8");
            LOGGER.debug("文件路径:" + returnFilePath);
            return returnFilePath;
        } catch (NullPointerException | UnsupportedEncodingException e) {
            LOGGER.warn(Thread.currentThread().hashCode() + "---- 无法找到文件:" + fileName + "。 返回 null");
        }
        return null;
    }

    /**
     * 从文件读取数据，返回String
     *
     * @param filePath the file path
     *
     * @return the resources of file
     */
    static String getResourcesOfFile(String filePath) {
        try {
            return CharStreams.toString(
                    new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath),
                            Charsets.UTF_8));
        } catch (IOException e) {
            LOGGER.warn("读取文件失败", e);
            return null;
        }
    }

    /**
     * 分割字符串
     *
     * @param commaSeparatedList 需要分割的字符串
     * @param string             the string
     *
     * @return 分割后的字符串列表 split list
     */
    static List<String> getSplitList(String commaSeparatedList, String string) {
        Iterable<String> split = Splitter.on(string).omitEmptyStrings().trimResults().split(commaSeparatedList);
        return Lists.newArrayList(split);
    }

    /**
     * 返回子串附有唯一的字符串UUID
     *
     * @param text      字符串
     * @param charCount 从0开始计数的字符长度
     *
     * @return 字符串 unique name
     */
    static String getUniqueName(String text, int charCount) {
        return (text + UUID.randomUUID()).substring(0, charCount);
    }

    /**
     * 返回文本附有唯一的字符串UUID
     *
     * @param text 字符串
     *
     * @return 字符串 unique name
     */
    static String getUniqueName(String text) {
        return text + UUID.randomUUID();
    }
}
