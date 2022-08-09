package com.chick.net.disk.driver.utils;

import com.chick.net.disk.driver.entity.NetDiskFile;

import java.util.List;

/**
 * @ClassName HtmlUtil
 * @Author xiaokexin
 * @Date 2022-08-04 22:14
 * @Description HtmlUtil
 * @Version 1.0
 */
public class HtmlUtil {

    public static String createTableHtml(List<NetDiskFile> netDiskFiles) {
        StringBuilder sbTrs = new StringBuilder();
        String tdHeaderName = getTagTd("名字");
        String tdHeaderSize = getTagTd("文件大小");
        String tdHeaderUpdateTime = getTagTd("更新时间");
        sbTrs.append(getTagTr(tdHeaderName + tdHeaderSize + tdHeaderUpdateTime));
        sbTrs.append(getTagTrSpace(getTagTd(" ")));
        for (NetDiskFile netDiskFile : netDiskFiles) {
            String nameA = getTagA(netDiskFile.getName(), netDiskFile.getPath());
            String tdA = getTagTd(nameA);
            String tdSize = getTagTd(netDiskFile.getSize());
            String tdUpdate = getTagTd(netDiskFile.getUpdatedAt());
            sbTrs.append(getTagTr(tdA + tdSize + tdUpdate));
        }
        return getTagTable(sbTrs.toString());
    }

    public static String getTagA(String text, String href) {
        StringBuilder a = new StringBuilder();
        a.append("<a style=\"text-decoration: none;\"");
        a.append("href=\"");
        a.append(href);
        a.append("\">");
        a.append(text);
        a.append("</a>");
        return a.toString();
    }

    public static String getTagTd(String text) {
        StringBuilder td = new StringBuilder();
        td.append("<td>");
        td.append(text);
        td.append("</td>");
        return td.toString();
    }

    public static String getTagTr(String... tds) {
        StringBuilder tr = new StringBuilder();
        tr.append("<tr >");
        for (String td : tds) {
            tr.append(td);
        }
        tr.append("</tr>");
        return tr.toString();
    }

    public static String getTagTrSpace(String... tds) {
        StringBuilder tr = new StringBuilder();
        tr.append("<tr style=\"height:10px;width:1px\">");
        for (String td : tds) {
            tr.append(td);
        }
        tr.append("</tr>");
        return tr.toString();
    }

    public static String getTagTable(String... trs) {
        StringBuilder table = new StringBuilder();
        table.append("<table style=\"text-align:left\">");
        for (String tr : trs) {
            table.append(tr);
        }
        table.append("</table>");
        return table.toString();
    }
}
