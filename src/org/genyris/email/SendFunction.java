// Copyright 2009 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.email;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.genyris.core.Constants;
import org.genyris.core.Exp;
import org.genyris.core.Pair;
import org.genyris.core.StrinG;
import org.genyris.exception.AccessException;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.ApplicableFunction;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;
import org.genyris.interp.UnboundException;

public class SendFunction extends ApplicableFunction {

    public SendFunction(Interpreter interp) {
        super(interp, Constants.PREFIX_EMAIL + "send", true);
    }

    public void postMail( Exp recipients, String subject, String message , String from, String server) throws MessagingException, AccessException
    {
        boolean debug = false;

         Properties props = new Properties();
         props.put("mail.smtp.host", server);

        // create some properties and get the default Session
        Session session = Session.getDefaultInstance(props, null);
        session.setDebug(debug);

        // create a message
        Message msg = new MimeMessage(session);

        // set the from and to address
        InternetAddress addressFrom = new InternetAddress(from);
        msg.setFrom(addressFrom);

        InternetAddress[] addressTo = new InternetAddress[recipients.length(NIL)]; 
        for (int i = 0; i < addressTo.length; i++) {
        
            addressTo[i] = new InternetAddress(recipients.car().toString());
        }
        msg.setRecipients(Message.RecipientType.TO, addressTo);
       

        // Optional : You can also set your custom headers in the Email if you Want
        msg.addHeader("MyHeaderName", "myHeaderValue");

        // Setting the Subject and Content Type
        msg.setSubject(subject);
        msg.setContent(message, "text/plain");
        Transport.send(msg);
    }

    public Exp bindAndExecute(Closure proc, Exp[] arguments,
            Environment envForBindOperations) throws GenyrisException {
        Class[] types = {Pair.class, StrinG.class, StrinG.class, StrinG.class, StrinG.class};
        checkArgumentTypes(types, arguments);
        try {
			postMail( arguments[0] , arguments[1].toString(), arguments[2].toString() , arguments[3].toString(), arguments[4].toString());
		} catch (MessagingException e) {
			throw new GenyrisException (e.getMessage());
		}
        
        
         return NIL;
       
    }
    public static void bindFunctionsAndMethods(Interpreter interpreter) throws UnboundException, GenyrisException {
        interpreter.bindGlobalProcedureInstance(new SendFunction(interpreter));
    }
}
