package com.citi.ets;

import java.nio.ByteBuffer;
import java.util.Date;
import com.citi.ets.cache.DateCache;
import com.citi.ets.cache.IntegerCache;

public class Trade implements Comparable<Trade> {

    private int facilityId;
    private String productType;
    private int hostId;
    private Date maturityDate;
    private double exposure;
    private byte[] srcInputBytes;

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(50);
        builder.append(facilityId);
        builder.append(",");
        builder.append(productType);
        builder.append(",");
        builder.append(hostId);
        builder.append(",");
        builder.append(DateCache.getInstance().formatDate(maturityDate));
        builder.append(",");
        builder.append(exposure);
        return builder.toString();
    }

    public void appendTo(final ByteBuffer buffer) {
        buffer.put(srcInputBytes);
        buffer.put(Sort.NEW_LINE);
    }

    public Trade(String input) {
        String[] fields = parse(input, 5, ',');
        srcInputBytes = input.getBytes();
        try {
            this.facilityId = IntegerCache.getInstance().getInteger(fields[0]);
            this.productType = fields[1];
            this.hostId = IntegerCache.getInstance().getInteger(fields[2]);
            this.maturityDate = DateCache.getInstance().getDate(fields[3]);
            this.exposure = Double.parseDouble(fields[4]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Trade(byte[] inputBytes) {
        String[] fields = parse(inputBytes, 5, ',');
        srcInputBytes = inputBytes;
        try {
            this.facilityId = IntegerCache.getInstance().getInteger(fields[0]);
            this.productType = fields[1];
            this.hostId = IntegerCache.getInstance().getInteger(fields[2]);
            this.maturityDate = DateCache.getInstance().getDate(fields[3]);
            this.exposure = Double.parseDouble(fields[4]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String[] parse(String input, int size, char sep) {
        String[] res = new String[size];
        int inputLen = input.length();
        int lastIndex = 0;
        int index = 0;
        for (int i = 0; i < inputLen; i++) {
            if (input.charAt(i) == sep) {
                res[index++] = input.substring(lastIndex, i);
                lastIndex = i + 1;
            }
        }

        if (input.charAt(inputLen - 1) != sep) {
            res[index++] = input.substring(lastIndex);
        }
        return res;
    }

    private String[] parse(byte[] input, int size, char sep) {
        String[] res = new String[size];
        int inputLen = input.length;
        int lastIndex = 0;
        int index = 0;
        for (int i = 0; i < inputLen; i++) {
            if (input[i] == (byte) ',') {
                res[index++] = new String(input, lastIndex, (i - lastIndex));
                lastIndex = i + 1;
            }
        }

        if (input[inputLen - 1] != (byte) ',') {
            res[index++] = new String(input, lastIndex, (inputLen - lastIndex));
        }
        return res;
    }

    public int getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(int facilityId) {
        this.facilityId = facilityId;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public int getHostId() {
        return hostId;
    }

    public void setHostId(int hostId) {
        this.hostId = hostId;
    }

    public Date getMaturityDate() {
        return maturityDate;
    }

    public void setMaturityDate(Date maturityDate) {
        this.maturityDate = maturityDate;
    }

    public double getExposure() {
        return exposure;
    }

    public void setExposure(double exposure) {
        this.exposure = exposure;
    }

    @Override
    public int compareTo(Trade o) {
        int res = this.facilityId > o.getFacilityId() ? 1 : (this.facilityId < o.getFacilityId() ? -1 : 0);
        if (res != 0) {
            return res;
        }

        res = this.productType.compareTo(o.getProductType());
        if (res != 0) {
            return res;
        }

        res = this.hostId > o.getHostId() ? 1 : (this.hostId < o.getHostId() ? -1 : 0);
        if (res != 0) {
            return res;
        }

        res = this.maturityDate.compareTo(o.getMaturityDate());
        if (res != 0) {
            return res;
        }

        res = Double.compare(this.exposure, o.getExposure());
        return res;
    }

}
