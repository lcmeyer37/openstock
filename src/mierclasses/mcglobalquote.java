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
public class mcglobalquote
{
    //classe que contem informacoes de um quote
    public String symbolstr;
    public String openstr;
    public String highstr;
    public String lowstr;
    public String pricestr;
    public String volumestr;
    public String latesttradingdaystr;
    public String previousclosestr;
    public String changestr;
    public String changepercentstr;
    
    public mcglobalquote
        (String sstr, String ostr, String hstr, String lstr, String pstr, String vstr, String ltdstr,
            String pcstr, String chstr, String chpstr)
    {
        symbolstr = sstr;
        openstr = ostr;
        highstr = hstr;
        lowstr = lstr;
        pricestr = pstr;
        volumestr = vstr;
        latesttradingdaystr = ltdstr;
        previousclosestr = pcstr;
        changestr = chstr;
        changepercentstr = chpstr;
    }
}

