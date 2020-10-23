package edu.uiuc.ncsa.security.core.util;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 10/23/20 at  3:41 PM
 */
public interface DebugConstants {
    /**
       * Turn off debugging
       */
       int DEBUG_LEVEL_OFF = 0;
      /**
       * ONly basic information should be displayed, such as milestones in the control flow
       */
       int DEBUG_LEVEL_INFO = 1;
      /**
       * Display warnings about the control flow and possibly harmful things
       */
       int DEBUG_LEVEL_WARN = 2;
      /**
       * Show errors or possible branch points of errors, but ones that still allow the control flow to continue
       */
       int DEBUG_LEVEL_ERROR = 3;
      /**
       * Show error that stop the control flow that probably lead the application to abort of be unrecoverable.
       */
       int DEBUG_LEVEL_SEVERE = 4;
      /**
       * Show detailed information about the execution so that detailed information about the control flow
       * can be seen. Note that this may be extremely verbose.
       */
       int DEBUG_LEVEL_TRACE = 5;

       String DEBUG_LEVEL_OFF_LABEL = "OFF";
       String DEBUG_LEVEL_INFO_LABEL = "INFO";
       String DEBUG_LEVEL_WARN_LABEL = "WARN";
       String DEBUG_LEVEL_ERROR_LABEL = "ERROR";
       String DEBUG_LEVEL_SEVERE_LABEL = "SEVERE";
       String DEBUG_LEVEL_TRACE_LABEL = "TRACE";
}
