package com.dacaspex.provider.tweakers;

import com.gargoylesoftware.css.parser.CSSErrorHandler;
import com.gargoylesoftware.css.parser.CSSException;
import com.gargoylesoftware.css.parser.CSSParseException;

/**
 * I am not interested in CSS errors, therefore they are suppressed.
 */
public class VoidCssErrorHandler implements CSSErrorHandler {
    @Override
    public void warning(CSSParseException e) throws CSSException {
        // Intentionally left blank
    }

    @Override
    public void error(CSSParseException e) throws CSSException {
        // Intentionally left blank
    }

    @Override
    public void fatalError(CSSParseException e) throws CSSException {
        // Intentionally left blank
    }
}
