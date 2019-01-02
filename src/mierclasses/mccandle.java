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
public class mccandle
{
    //classe que contem informacoes de um candle
    public String timestampstr;
    public String openstr;
    public String highstr;
    public String closestr;
    public String lowstr;
    public String volumestr;
    
    public double opend;
    public double highd;
    public double closed;
    public double lowd;
    public double volumed;
    public java.util.Date timestampdate;
    
    public mccandle(String tsstr, String ostr, String hstr, String cstr, String lstr, String vstr)
    {
        timestampstr = tsstr;
        openstr = ostr;
        highstr = hstr;
        closestr = cstr;
        lowstr = lstr;
        volumestr = vstr;
        
        opend = Double.valueOf(openstr);
        highd = Double.valueOf(highstr);
        closed = Double.valueOf(closestr);
        lowd = Double.valueOf(lowstr);
        volumed = Double.valueOf(volumestr);
        timestampdate = interpretartimestamp(timestampstr);
        

    }
    
    java.util.Date interpretartimestamp(String timestampstr)
    {
        java.util.Date timestampretornar;
        
        //parser caso 2018-12-21 16:00:00
        Integer ano = -1;
        Integer mes = -1;
        Integer dia = -1;
        Integer hora = -1;
        Integer minuto = -1;
        Integer segundo = -1;
        
        try
        {
            ano = Integer.parseInt((timestampstr.split(" ")[0]).split("-")[0]);
        }
        catch (java.lang.ArrayIndexOutOfBoundsException excecao)
        {
            ano = -1;
        }
        
        try
        {
            mes = Integer.parseInt((timestampstr.split(" ")[0]).split("-")[1]);
        }
        catch (java.lang.ArrayIndexOutOfBoundsException excecao)
        {
            mes = -1;
        }
        
        try
        {
            dia = Integer.parseInt((timestampstr.split(" ")[0]).split("-")[2]);
        }
        catch (java.lang.ArrayIndexOutOfBoundsException excecao)
        {
            dia = -1;
        }
        
        try
        {
            hora = Integer.parseInt((timestampstr.split(" ")[1]).split(":")[0]);
            //mierfuncoeshelper.mostrarmensagem(hora.toString());
        }
        catch (java.lang.ArrayIndexOutOfBoundsException excecao)
        {
            hora = -1;
        }
        
        try
        {
            minuto = Integer.parseInt((timestampstr.split(" ")[1]).split(":")[1]);
            //mierfuncoeshelper.mostrarmensagem(minuto.toString());
        }
        catch (java.lang.ArrayIndexOutOfBoundsException excecao)
        {
            minuto = -1;
        }
        
        try
        {
            segundo = Integer.parseInt((timestampstr.split(" ")[1]).split(":")[2]);
            //mierfuncoeshelper.mostrarmensagem(segundo.toString());
        }
        catch (java.lang.ArrayIndexOutOfBoundsException excecao)
        {
            segundo = -1;
        }

        if ((hora > -1) == false)
            timestampretornar = new java.util.Date(ano-1900,mes-1,dia);
        else
            timestampretornar = new java.util.Date(ano-1900,mes-1,dia,hora,minuto,segundo);
        
        return timestampretornar;
    }
}
