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
public class mcjtextareahandler
{
    //classe utilizada para handling do textarea de output
    javax.swing.JTextArea jtextarea;
    
    public mcjtextareahandler(javax.swing.JTextArea jta)
    {
        jtextarea = jta;
    }
    
    public void print(String printstring)
    {
        jtextarea.append(printstring);
    }
}
