/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 *
 * @author lucasmeyer
 */
public class OpenStock extends Application
{
    
    @Override
    public void start(Stage primaryStage)
    {
        try
        {
            java.util.concurrent.TimeUnit.MILLISECONDS.sleep(2000);
            TelaPrincipal tprincipal = new TelaPrincipal();
            tprincipal.show();
        }
        catch (Exception ex)
        {
            mierclasses.mcfuncoeshelper.mostrarmensagem("A problem has occurred when initializing Open Stock. Exception: " + ex.getLocalizedMessage());
        }

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        launch(args);
    }
    
}
