/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mathpiper.interpreters;

import org.mathpiper.lisp.cons.ConsPointer;

/**
 * This class is used by an {@link Interpreter} to send the results of an evaluation to
 * client code.
 */
public class EvaluationResponse {
    private String result = "";
    private String sideEffects = "";
    private String exceptionMessage = "";
    private boolean exceptionThrown = false;
    private Exception exception = null;
    private int lineNumber;
    private String sourceFileName = "";
    private Object object = null;
    private ConsPointer resultList = null;
            
    private EvaluationResponse()
    {
    }

    /**
     * A static factory method which is used to crerate new EvaluationResponse objects.
     *
     * @return a new EvaluationResponse
     */
    public static EvaluationResponse newInstance()
    {
        return new EvaluationResponse();
    }

    /**
     * Returns the name of the source file in which an error occurred.
     *
     * @return the name of the source file
     */
    public String getSourceFileName()
    {
        return sourceFileName;
    }

    /**
     * Sets the name of the source file in which an error occurred.
     *
     * @param  name of the source file
     */
    public void setSourceFileName(String sourceFileName)
    {
        this.sourceFileName = sourceFileName;
    }

    /**
     * Returns the line number near where an error occurred.
     *
     * @return the line number near where an error occurred
     */
    public int getLineNumber()
    {
        return lineNumber;
    }

    /**
     * Sets the line number near where an error occurred.
     *
     * @param  lineNumber the line number near where an error occurred
     */
    public void setLineNumber(int lineNumber)
    {
        this.lineNumber = lineNumber;
    }

    /**
     * Returns the result of the evaluation.
     *
     * @return the result of the evaluation
     */
    public String getResult()
    {
        return result;
    }

    /**
     * Sets the result of the evaluation.
     *
     * @param result the result of the evaluation
     */
    public void setResult(String result)
    {
        this.result = result.trim();
    }

    /**
     * Returns any side effects generated by the evaluation.
     * 
     * @return any side effects generated by the evaluation
     */
    public String getSideEffects()
    {
        return sideEffects;
    }

    /**
     * Sets any side effects generated by the evaluation.
     * 
     * @param sideEffects any side effects generated by the evaluation
     */
    public void setSideEffects(String sideEffects)
    {
        this.sideEffects = sideEffects;
    }

    /**
     * Returns the exception message generated by the evaluation (if present).
     *
     * @return the exception message
     */
    public String getExceptionMessage()
    {
        return exceptionMessage;
    }

    /**
     * Sets the exception message generated by the evaluation (if present).
     *
     * @param exceptionMessage the exception message
     */
    public void setExceptionMessage(String exceptionMessage)
    {
        if(exceptionMessage != null)
        {
            this.exceptionMessage = exceptionMessage.trim();
        }
    }

     /**
     * Returns the exception object thrown by the evaluation (if present).
     *
     * @return the exception object
     */
    public Exception getException()
    {
        return exception;
    }

    /**
     * Sets the exception object thrown by the evaluation (if present).
     *
     * @param exception the exception object
     */
    public void setException(Exception exception)
    {
        this.exceptionThrown = true;
        this.exception = exception;
    }

    /**
     * Allows the client to determine if the evaluation threw an exception.
     *
     * @return {@code true} if an exception was thrown and {@code false} otherwise
     */
    public boolean isExceptionThrown()
    {
        return exceptionThrown;
    }


    /**
     * Allows the user to obtain a Java object from a function.
     *
     * @return a Java object if one is available to return to the user.
     */
    public Object getObject()
    {
        return object;
    }


    /**
     * Sets a Java object to be returned to the user..
     *
     * @param exception the exception object
     */
    public void setObject(Object object)
    {
        this.object = object;
    }



    /**
     * Allows the user to obtain the result list.
     *
     * @return a Java object if one is available to return to the user.
     */
    public ConsPointer getResultList() {
        return resultList;
    }


    /**
     * Sets the result list to be returned to the user..
     *
     * @param exception the exception object
     */
    public void setResultList(ConsPointer resultList) {
        this.resultList = resultList;
    }



}//end class.
