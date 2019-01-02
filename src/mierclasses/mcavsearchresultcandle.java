/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mierclasses;

/**
 *
 * @author lucasmeyer
 */
public class mcavsearchresultcandle
{
    //classe que contem best maches encontradas pela query de busca do alpha vantage
    public String symbolstr;
    public String namestr;
    public String typestr;
    public String regionstr;
    public String marketopenstr;
    public String marketclosestr;
    public String timezonestr;
    public String currencystr;
    public String matchscorestr;
    
    public mcavsearchresultcandle(String sstr, String nstr, String tstr, String rstr, String mostr, String mcstr,
            String tzstr, String curstr, String matchsstr)
    {
        symbolstr = sstr;
        namestr = nstr;
        typestr = tstr;
        regionstr = rstr;
        marketopenstr = mostr;
        marketclosestr = mcstr;
        timezonestr = tzstr;
        currencystr = curstr;
        matchscorestr = matchsstr;
    }
}
