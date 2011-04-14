/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.squilla.service;

/**
 *
 * @author Fantom
 */
public interface ProcessListener {

    public void done(Object o, Object result);

    public void failed(Object o, Object reason);
}
