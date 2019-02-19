package train.common.mtc;

public class RouteStation {
    public Integer stationID;
    public String stationName;
    public Integer waitTime;
    public boolean endOfLine;

    public RouteStation(Integer si, String sn, Integer wt, Boolean eol) {
        stationID = si;
        stationName = sn;
        waitTime = wt;
        endOfLine = eol;
    }

    public Integer getStationID() {
        return stationID;
    }

    public String getStationName() {
        return stationName;
    }

    public Integer getWaitTime() { return waitTime; }

    public Boolean getEndOfLine() {return endOfLine;}
}
