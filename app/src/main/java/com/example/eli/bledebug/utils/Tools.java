package com.example.eli.bledebug.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;

/**
 * Created by Administrator on 2016/3/4.
 */
public class Tools {

    public static String FormatString(int value) {
        String strValue = "";
        byte[] ary = intToByteArray(value);
        for (int i = ary.length - 1; i >= 0; i--) {
            strValue += (ary[i] & 0xFF);
            if (i > 0) {
                strValue += ".";
            }
        }
        return strValue;
    }

    public static byte[] intToByteArray(int value) {
        byte[] b = new byte[4];
        for (int i = 0; i < 4; i++) {
            int offset = (b.length - 1 - i) * 8;
            b[i] = (byte) ((value >>> offset) & 0xFF);
        }
        return b;
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public static String displayNmuber(float airPress) {
        BigDecimal bd = new BigDecimal(airPress);
        bd = bd.setScale(1, BigDecimal.ROUND_HALF_UP);
        return bd.toString();
    }

    public static float byte2float(int[] b, int index) {
        if (index + 3 < b.length) {
            int l;
            l = b[index + 3];
            l &= 0xff;
            l |= ((long) b[index + 2] << 8);
            l &= 0xffff;
            l |= ((long) b[index + 1] << 16);
            l &= 0xffffff;
            l |= ((long) b[index + 0] << 24);
            return Float.intBitsToFloat(l);
        } else {
            return 0;
        }
    }

    public static byte[] creatNewByteValue(byte[] val) {
        byte[] copy = copyOfRange(val, 3, val.length - 2);
        int j = 0;
        for (byte i = 0; i < copy.length; i++) {
            if ((copy[i] & 0xff) != 0) {
                j++;
            }
        }
        byte[] newarr = new byte[j];
        j = 0;
        for (byte i = 0; i < copy.length; i++) {
            if ((copy[i] & 0xff) != 0) {
                newarr[j] = copy[i];
                j++;
            }
        }
        for (byte i = 0; i < newarr.length; i++) {
            System.out.print(newarr[i] + ",");
        }
        return newarr;
    }

    /**
     * 2  * 获取版本号
     * 3  * @return 当前应用的版本号
     * 4
     */


    public static String getVersion(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            String version = info.versionName;
            return version;

        } catch (Exception e) {
            e.printStackTrace();
            return "";

        }

    }

    public final static short getShort(byte[] buf, boolean asc) {
        if (buf == null) {
            throw new IllegalArgumentException("byte array is null!");
        }
        if (buf.length > 2) {
            throw new IllegalArgumentException("byte array size > 2 !");
        }
        short r = 0;
        if (asc)
            for (int i = buf.length - 1; i >= 0; i--) {
                r <<= 8;
                r |= (buf[i] & 0x00ff);
            }
        else
            for (int i = 0; i < buf.length; i++) {
                r <<= 8;
                r |= (buf[i] & 0x00ff);
            }
        return r;
    }

    /**
     * 从指定数组的copy一个子数组并返回
     *
     * @param original of type byte[] 原数组
     * @param from     起始点
     * @param to       结束点
     * @return 返回copy的数组
     */

    public static byte[] copyOfRange(byte[] original, int from, int to) {

        int newLength = to - from;

        if (newLength < 0)

            throw new IllegalArgumentException(from + " > " + to);

        byte[] copy = new byte[newLength];

        System.arraycopy(original, from, copy, 0,

                Math.min(original.length - from, newLength));

        return copy;

    }

    /**
     * byte数组转换为无符号short整数
     *
     * @param bytes byte数组
     * @param off   开始位置
     * @return short整数
     */
    public static int byte2ToUnsignedShort(int[] bytes, int off) {
        int high = bytes[off];
        int low = bytes[off + 1];
        return (high << 8 & 0xFF00) | (low & 0xFF);
    }

    public static byte[] readBytes(InputStream in) throws IOException {
        byte[] temp = new byte[in.available()];
        byte[] result = new byte[0];
        int size = 0;
        while ((size = in.read(temp)) != -1) {
            byte[] readBytes = new byte[size];
            System.arraycopy(temp, 0, readBytes, 0, size);
            result = Tools.mergeArray(result, readBytes);
        }
        return result;
    }

    // byte转十六进制字符串
    public static String bytes2HexString(byte[] bytes) {
        String ret = "";
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            ret += hex.toUpperCase() + " ";
        }
        return ret;
    }

    private static byte[] reverseBytes(byte[] a) {
        int len = a.length;
        byte[] b = new byte[len];
        for (int k = 0; k < len; k++) {
            b[k] = a[a.length - 1 - k];
        }
        return b;
    }

    /***
     * 合并字节数组
     * .	     * @param a
     *
     * @return
     */
    public static byte[] mergeArray(byte[]... a) {
        // 合并完之后数组的总长度
        int index = 0;
        int sum = 0;
        for (int i = 0; i < a.length; i++) {
            sum = sum + a[i].length;
        }
        byte[] result = new byte[sum];
        for (int i = 0; i < a.length; i++) {
            int lengthOne = a[i].length;
            if (lengthOne == 0) {
                continue;
            }
            // 拷贝数组
            System.arraycopy(a[i], 0, result, index, lengthOne);
            index = index + lengthOne;
        }
        return result;
    }

    public static boolean isWifiConnect(Context context) {
        ConnectivityManager connManager =
                (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return mWifi.isConnected();
    }

    public static int px2sp(Context context, float pxValue) {
        return (int) (pxValue / context.getResources().getDisplayMetrics().scaledDensity + 0.5F);
    }

    /**
     * 获取所得数据类型
     */
    public static String getType(int x, String strPort) {
        switch (x) {
            case 12:
            case 17:
                return "Temp-" + strPort;
            case 100:
            case 200:
            case 115:
                return "Oxy-" + strPort;
            case 120:
            case 220:
                return "Stab-" + strPort;
            case 215:
                return "Plau-" + strPort;
            default:
                return strPort;
        }
    }

    /**
     * 获取所得数据单位
     */

    public static String getUnit(int x) {
        switch (x) {
            case 0:
            case 5:
                return "Counts";
            case 1:
                return "°C";
            case 2:
                return "mbar";
            case 3:
                return "mg/l";
            case 4:
                return "ns";
            case 6:
                return "mV";
            case 7:
                return "%AirSAT";
            case 8:
            case 12:
                return "ppm";
            case 9:
                return "Vol.% ";
            case 10:
                return "% STA";
            case 13:
            case 14:
                return "ppb";
            default:
                return "";
        }
    }

    /**
     * 获取端口状态
     * 0 = OK
     * 1 = out of specification
     * 2 = failure
     * 3 = check function
     * 4 = maintenance required
     */
    public static String getPortStatus(int x) {
        switch (x) {
            case 0:
                return "OK";
            case 1:
                return "out of specification";
            case 2:
                return "failure";
            case 3:
                return "check function";
            case 4:
                return "maintenance required";
            default:
                return "";
        }
    }

    /**
     * 获取平均值
     *
     * @param channel
     * @return
     */
    public static int[] getChannel(int channel) {
        switch (channel) {
            case 0:
                return new int[]{0};

            case 1:
                return new int[]{1};

            case 2:
                return new int[]{2};

            case 3:
                return new int[]{1, 2};

            case 4:
                return new int[]{3};

            case 5:
                return new int[]{1, 3};

            case 6:
                return new int[]{2, 3};

            case 7:
                return new int[]{1, 2, 3};

            case 8:
                return new int[]{4};

            case 9:
                return new int[]{1, 4};

            case 10:
                return new int[]{2, 4};

            case 11:
                return new int[]{1, 2, 4};

            case 12:
                return new int[]{3, 4};

            case 13:
                return new int[]{1, 3, 4};

            case 14:
                return new int[]{2, 3, 4};

            case 15:
                return new int[]{1, 2, 3, 4};

        }
        return new int[]{0};
    }

    /**
     * Description of Measurement Status
     * 1 (0x01) OK OK
     * 2 (0x02) Measuring cap to old - Please change the measuring
     * cap!
     * Maintenan......
     * ce
     */
    public static final int OK = 0;
    public static final int BLUT = 1;
    public static final int YELLOW = 2;
    public static final int RED = 3;


    public static int getMeasurementStatus(int x) {
        switch (x) {
            case 1:
                return OK;
            case 2:
            case 3:
            case 4:
            case 5:
                return BLUT;
            case 145:
            case 146:
            case 244:
            case 245:
            case 246:
            case 247:
                return YELLOW;
            case 136:
            case 137:
            case 144:
            case 250:
            case 253:
            case 254:
            case 255:
            case 240:
            case 241:
            case 242:
            case 243:
                return RED;
            default:
                return -1;
        }
    }
}
