/*
 * Copyright (C) 2019 lucasmeyer
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package mierclasses;

/**
 *
 * @author lucasmeyer
 */
public class mcquote
{
    //funcao para receber informacoes do quote
    public String symbolstr;
    public String companynamestr;
    public String openstr;
    public String opentimestr;
    public String closestr;
    public String closetimestr;
    public String highstr;
    public String lowstr;
    public String latestvolumestr;
    public String previousclosestr;
    
    public double closed;
    public double previousclosed;
    public java.util.Date opentimedate;
    public java.util.Date closetimedate;
    
    public mcquote
        (
            String sstr, String cnstr, String ostr, String otstr, 
            String cstr, String ctstr,String hstr, String lstr, 
            String lvstr, String pcstr
        )
    {
        symbolstr = sstr;
        companynamestr = cnstr;
        openstr = ostr;
        opentimestr = otstr;
        closestr = cstr;
        closetimestr = ctstr;
        highstr = hstr;
        lowstr = lstr;
        latestvolumestr = lvstr;
        previousclosestr = pcstr;
        
        closed = Double.valueOf(closestr);
        previousclosed = Double.valueOf(previousclosestr);
        opentimedate = new java.util.Date(Long.valueOf(opentimestr));
        closetimedate = new java.util.Date(Long.valueOf(closetimestr));
    }
    
}
