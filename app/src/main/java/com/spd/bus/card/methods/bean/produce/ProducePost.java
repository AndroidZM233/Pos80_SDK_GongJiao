package com.spd.bus.card.methods.bean.produce;

/**
 * Created by 张明_ on 2019/3/7.
 * Email 741183142@qq.com
 */
public class ProducePost {

    /**
     * type : 2200AQ
     * posId : 123
     * route : 11
     * data : {'secret':'DB623AFEBF6B5CA8DC500449D0B59AA7','reqData':[{'outTradeNo':'0645_17510645_20180311050443','deviceId':'17510645','driverCardNo':'3000000151654258','cardType':'T0120000','userId':'2088422423867101','carryCode':'031364','busGroupCode':'0025','companyCode':'03','stationName':'','cardId':'1200000846039941','sellerSignTime':'','seq':'71b9','driverSignTime':'20180310051132','areaCode':'','price':'150','stationId':'','actualOrderTime':'2018-03-11 05:04:43','cardData':'31','actualPrice':'150','lineCode':'0645','record':'10E202010058323038383432323432333836373130315AAD82EA025807D000000000000000000000000002BA57F29F424209612D7545D3B0E6A492E937E7F59BC4D442543031323030303010313230303030303834363033393934310131483046022100E96C5D2BEEFCB24F55F64D04D76CE7195DBCE922164FAB2A30A80B8E88EE157602210087417B2D0919CF4F7ED6B727CDB5DAA2849E023BE89440B65F8CDA3C46A2CD97045AA4486A373035021900F6FE020E25E511BCE109720BD84963D9B819E24AAD91C12102182C59868123F42B1E68A15281479BA8037E09DAC7BB272F2200617B22706F735F6964223A223137353130363435222C2274797065223A2253494E474C45222C227375626A656374223A2230363435222C227265636F72645F6964223A22303634355F31373531303634355F3230313830333131303530343433227D00045AA4486B0010D2A54278A724311BFD83D2D38719C59C'}]}
     */

    private String type;
    private String posId;
    private String route;
    private String data;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPosId() {
        return posId;
    }

    public void setPosId(String posId) {
        this.posId = posId;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
