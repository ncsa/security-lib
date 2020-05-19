package edu.uiuc.ncsa.demo;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.GridLayout;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.Window;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.FileDialogBuilder;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.swing.TerminalEmulatorColorConfiguration;
import com.googlecode.lanterna.terminal.swing.TerminalEmulatorPalette;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;

import static com.googlecode.lanterna.input.KeyType.Escape;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 5/7/20 at  3:50 PM
 */
public class GUIDemo {
    public static void main(String[] args) throws IOException {
        // Note UnixTerminal is supported BUT will freee the IDE because of
        // how it uses standard out. Write everything letting Lanterna decide
        // what works then switch when running in xterm.

        // Next three are required to start pretty much anything
        DefaultTerminalFactory factory = new DefaultTerminalFactory();
        // All this to set the background color of the terminal...
        TerminalEmulatorPalette myPallette = new TerminalEmulatorPalette(
                Color.WHITE, // sets default
                Color.RED, // sets default Bright
                Color.BLUE, // sets background. This is the only reason for this constructor
                Color.black,
                Color.BLACK,
                Color.red,
                Color.RED,
                Color.green,
                Color.GREEN,
                Color.yellow,
                Color.YELLOW,
                Color.blue,
                Color.BLUE,
                Color.magenta,
                Color.MAGENTA,
                Color.cyan,
                Color.CYAN,
                Color.white,
                Color.WHITE

        );

        //TerminalEmulatorPalette palette = new TerminalEmulatorPalette()
        TerminalEmulatorColorConfiguration colorConfig = TerminalEmulatorColorConfiguration.newInstance(myPallette);
        factory.setTerminalEmulatorColorConfiguration(colorConfig);
        Terminal terminal = factory.createTerminal();
        System.out.println("terminal is " + terminal.getClass().getSimpleName());
        Screen screen = new TerminalScreen(terminal);


        // sets up a resize listener in case the user changes the size.
  /*      terminal.addResizeListener((terminal1, newSize) -> {
            // Be careful here though, this is likely running on a separate thread. Lanterna is threadsafe in
            // a best-effort way so while it shouldn't blow up if you call terminal methods on multiple threads,
            // it might have unexpected behavior if you don't do any external synchronization
            textGraphics.drawLine(5, 3, newSize.getColumns() - 1, 3, ' ');
            textGraphics.putString(5, 3, "Terminal Size: ", SGR.BOLD);
            textGraphics.putString(5 + "Terminal Size: ".length(), 3, newSize.toString());
            try {
                terminal1.flush();
            } catch (IOException e) {
                // Not much we can do here
                throw new RuntimeException(e);
            }
        });*/

        TextGraphics textGraphics = screen.newTextGraphics();
        //textGraphics.setForegroundColor(TextColor.ANSI.YELLOW);
        screen.startScreen();
        screen.refresh(); // clear out any cruft in object properly.
              //textGraphics.
        // Only one of these should be uncommented at any time since they are separate demos.
       // showStuff(textGraphics);
       // terminal.flush();
        // helloWorld(screen);
        // userInput2(terminal, screen, textGraphics);
        //  testBox(screen);
        terminalInput(terminal, screen, textGraphics);
        screen.stopScreen();
    }

    /**
     * Writes text an a couple fo graphics with the text graphics
     *
     * @param textGraphics
     */
    protected static void showStuff(TextGraphics textGraphics) {
        textGraphics.putString(10, 10, "Hello World");
        textGraphics.drawRectangle(new TerminalPosition(5, 5), new TerminalSize(10, 10), '*');
        textGraphics.drawTriangle(new TerminalPosition(0, 0), new TerminalPosition(15, 7), new TerminalPosition(12, 20), Symbols.DIAMOND);
    }

    /**
     * Next is taken verbatim from
     * <a href="https://github.com/mabe02/lanterna/blob/master/src/test/java/com/googlecode/lanterna/tutorial/Tutorial04.java">this laterna tutorial.</a>
     * It will create a groovy floating text window with border. The contents have a few labels and a text box.
     * <h3>Notes</h3>
     * <ul>
     *     <li>Tab between items.</li>
     *     <li>A return activates item</li>
     *     <li>Looks like accelerators are enabled (first character highlighted) but no code to do that, To close, tab to "Close"
     *     button and hit enter.</li>
     * </ul>
     *
     * @param screen
     * @throws IOException
     */
    protected static void testBox(Screen screen) throws IOException {
        // ------------------
        // Note that this puts windows on a screen but altering the screen might give strange results,
        // because these GUI components are not aware of it. Think of the screen as working like
        // the Swing JDesktop pane and being a container.
        // All the components (e.g.Panel) are Lanterna NOT Java!
        // ------------------
        final WindowBasedTextGUI textGUI = new MultiWindowTextGUI(screen);

                  /*
                  Creating a new window is relatively uncomplicated, you can optionally supply a title for the window
                   */
        final Window window = new BasicWindow("My Root Window");
        

                  /*
                  The window has no content initially, you need to call setComponent to populate it with something. In this
                  case, and quite often in fact, you'll want to use more than one component so we'll create a composite
                  'Panel' component that can hold multiple sub-components. This is where we decide what the layout manager
                  should be.
                   */
        Panel contentPanel = new Panel(new GridLayout(2));
        

        /*
         * Lanterna contains a number of built-in layout managers, the simplest one being LinearLayout that simply
         * arranges components in either a horizontal or a vertical line. In this tutorial, we'll use the GridLayout
         * which is based on the layout manager with the same name in SWT. In the constructor above we have
         * specified that we want to have a grid with two columns, below we customize the layout further by adding
         * some spacing between the columns.
         */
        GridLayout gridLayout = (GridLayout) contentPanel.getLayoutManager();
        gridLayout.setHorizontalSpacing(3);

                  /*
                  One of the most basic components is the Label, which simply displays a static text. In the example below,
                  we use the layout data field attached to each component to give the layout manager extra hints about how it
                  should be placed. Obviously the layout data has to be created from the same layout manager as the container
                  is using, otherwise it will be ignored.
                   */
        Label title = new Label("This is a label that spans two columns");
        title.setLayoutData(GridLayout.createLayoutData(
                GridLayout.Alignment.BEGINNING, // Horizontal alignment in the grid cell if the cell is larger than the component's preferred size
                GridLayout.Alignment.BEGINNING, // Vertical alignment in the grid cell if the cell is larger than the component's preferred size
                true,       // Give the component extra horizontal space if available
                false,        // Give the component extra vertical space if available
                2,                  // Horizontal span
                1));                  // Vertical span
        contentPanel.addComponent(title);

                  /*
                  Since the grid has two columns, we can do something like this to add components when we don't need to
                  customize them any further.
                   */
        contentPanel.addComponent(new Label("Text Box (aligned)"));
        contentPanel.addComponent(
                new TextBox()
                        .setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.BEGINNING, GridLayout.Alignment.CENTER)));

                  /*
                  Here is an example of customizing the regular text box component so it masks the content and can work for
                  password input.
                   */
        contentPanel.addComponent(new Label("Password Box (right aligned)"));
        contentPanel.addComponent(
                new TextBox()
                        .setMask('*')
                        .setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.END, GridLayout.Alignment.CENTER)));

                  /*
                  While we are not going to demonstrate all components here, here is an example of combo-boxes, one that is
                  read-only and one that is editable.
                   */
        contentPanel.addComponent(new Label("Read-only Combo Box (forced size)"));
        List<String> timezonesAsStrings = new ArrayList<>(Arrays.asList(TimeZone.getAvailableIDs()));
        ComboBox<String> readOnlyComboBox = new ComboBox<>(timezonesAsStrings);
        readOnlyComboBox.setReadOnly(true);
        readOnlyComboBox.setPreferredSize(new TerminalSize(20, 1));
        contentPanel.addComponent(readOnlyComboBox);

        contentPanel.addComponent(new Label("Editable Combo Box (filled)"));
        contentPanel.addComponent(
                new ComboBox<>("Item #1", "Item #2", "Item #3", "Item #4")
                        .setReadOnly(false)
                        .setLayoutData(GridLayout.createHorizontallyFilledLayoutData(1)));

                  /*
                  Some user interactions, like buttons, work by registering callback methods. In this example here, we're
                  using one of the pre-defined dialogs when the button is triggered.
                   */
        contentPanel.addComponent(new Label("Button (centered)"));
        contentPanel.addComponent(new Button("Button", () -> MessageDialog.showMessageDialog(textGUI, "MessageBox", "This is a message box", MessageDialogButton.OK)).setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.CENTER, GridLayout.Alignment.CENTER)));

                  /*
                  Close off with an empty row and a separator, then a button to close the window
                   */
        contentPanel.addComponent(
                new EmptySpace()
                        .setLayoutData(
                                GridLayout.createHorizontallyFilledLayoutData(2)));
        contentPanel.addComponent(
                new Separator(Direction.HORIZONTAL)
                        .setLayoutData(
                                GridLayout.createHorizontallyFilledLayoutData(2)));
        contentPanel.addComponent(
                new Button("Close", window::close).setLayoutData(
                        GridLayout.createHorizontallyEndAlignedLayoutData(2)));

                  /*
                  We now have the content panel fully populated with components. A common mistake is to forget to attach it to
                  the window, so let's make sure to do that.
                   */
        window.setComponent(contentPanel);

                  /*
                  Now the window is created and fully populated. As discussed above regarding the threading model, we have the
                  option to fire off the GUI here and then later on decide when we want to stop it. In order for this to work,
                  you need a dedicated UI thread to run all the GUI operations, usually done by passing in a
                  SeparateTextGUIThread object when you create the TextGUI. In this tutorial, we are using the conceptually
                  simpler SameTextGUIThread, which essentially hijacks the caller thread and uses it as the GUI thread until
                  some stop condition is met. The absolutely simplest way to do this is to simply ask lanterna to display the
                  window and wait for it to be closed. This will initiate the event loop and make the GUI functional. In the
                  "Close" button above, we tied a call to the close() method on the Window object when the button is
                  triggered, this will then break the even loop and our call finally returns.
                   */
        textGUI.addWindowAndWait(window);

                  /*
                  When our call has returned, the window is closed and no longer visible. The screen still contains the last
                  state the TextGUI left it in, so we can easily add and display another window without any flickering. In
                  this case, we want to shut down the whole thing and return to the ordinary prompt. We just need to stop the
                  underlying Screen for this, the TextGUI system does not require any additional disassembly.
                   */
    }

    public static class MyWindow extends BasicWindow {
        public MyWindow() {
            super("My Window!");
            setComponent(new Button("Exit", new Runnable() {
                    @Override
                    public void run() {
                        MyWindow.this.close();
                    }
                }));
        }
    }

    static File input;
    public static void componentTest(Screen screen, TextGraphics textGraphics) {
        final WindowBasedTextGUI textGUI = new MultiWindowTextGUI(screen);
        final Window window = new MyWindow();
        Panel contentPanel = new Panel(new GridLayout(2));

        contentPanel.addComponent(new Button("open file", new Runnable() {
            @Override
            public void run() {
                 input = new FileDialogBuilder()
                        .setTitle("Open File")
                        .setDescription("Choose a file")
                        .setActionLabel("Open")
                        .build()
                        .showDialog(textGUI);

            }
        }));


        window.setComponent(contentPanel);
        textGUI.addWindowAndWait(window);
        window.close();

    }

    /**
     * Use terminal like standard text mode. First try and stuff just gets tested here.
     *
     * @param terminal
     * @param screen
     * @param textGraphics
     * @throws IOException
     */
    protected static void userInput2(Terminal terminal, Screen screen, TextGraphics textGraphics) throws IOException {


        textGraphics.putString(5, 4, "Last Keystroke: ", SGR.BOLD);
        textGraphics.putString(5 + "Last Keystroke: ".length(), 4, "<Pending>");
        terminal.flush();
        int row = 0;
        TerminalPosition cursor = terminal.getCursorPosition();

        KeyStroke keyStroke = terminal.readInput();
        while (keyStroke.getKeyType() != Escape) {
            System.out.println("Char in = " + keyStroke.getCharacter());
            textGraphics.drawLine(5, 4, terminal.getTerminalSize().getColumns() - 1, 4, ' ');
            textGraphics.putString(5, 4, "Last Keystroke: ", SGR.BOLD);
            textGraphics.putString(5, 4, "cursor at: " + cursor.getColumn() + ", " + cursor.getRow(), SGR.BOLD);
            textGraphics.putString(5 + "Last Keystroke: ".length(), 4, keyStroke.toString());
            terminal.flush();
            keyStroke = terminal.readInput();
        }

    }

    public static void helloWorld(Screen screen) {

        // Create panel to hold components
        Panel panel = new Panel();
        panel.setLayoutManager(new GridLayout(2));

        panel.addComponent(new Label("Forename"));
        panel.addComponent(new TextBox());

        panel.addComponent(new Label("Surname"));
        panel.addComponent(new TextBox());

        panel.addComponent(new EmptySpace(new TerminalSize(0, 0))); // Empty space underneath labels
        panel.addComponent(new Button("Submit"));

        // Create window to hold the panel
        BasicWindow window = new BasicWindow();
        window.setComponent(panel);

        // Create gui and start gui
        MultiWindowTextGUI gui = new MultiWindowTextGUI(screen, new DefaultWindowManager(), new EmptySpace(TextColor.ANSI.BLUE));
        gui.addWindowAndWait(window);

    }

    /**
     * Another example.  Tries to read user input and put on screen. If you hit up/down arros the color changes
     * Escape exits.
     * This writes using the terminal, not text graphics. it uses text graphics to change colors.
     * <br>Note that this cannot do things like set th terminal color (just text). It is very basic,
     * but it would allow for things like command history and completion.
     *
     * @param terminal
     * @param screen
     * @param textGraphics
     * @throws IOException
     */
    protected static void terminalInput(Terminal terminal, Screen screen, TextGraphics textGraphics) throws IOException {
        boolean keepRunning = true;
        int line = 0;
        StringBuffer stringBuffer = new StringBuffer();
        terminal.setCursorVisible(true);
        terminal.setCursorPosition(0, 0);

        terminal.setForegroundColor(TextColor.ANSI.YELLOW);
        // String is of form # RGB where each color is a hex number 0 - 256 aka x0 - xFF
        //terminal.setForegroundColor(TextColor.RGB.Factory.fromString("#0000FF"));
        terminal.enableSGR(SGR.BOLD);
        List<String> commandBuffer = new ArrayList<>();
        int currentBufferPosition = 0;
        while (keepRunning) {
            KeyStroke keyStroke = terminal.readInput(); //Block input or this does not draw right at all.
            if (keyStroke != null) {
                switch (keyStroke.getKeyType()) {
                    case MouseEvent:
                        System.out.println("Yo!" + keyStroke);
                        break;
                    case Escape:
                        componentTest(screen, textGraphics);
                        println(terminal,0,line,input==null?"(empty)":input.getAbsolutePath());
                        keepRunning = false;
                        break;
                    case EOF: // If there is some issue shutting down the JVM, it starts spitting these out. Just exit.
                        keepRunning = false;
                        break;
                    case Enter:
                        System.out.println("printing buffer = " + stringBuffer);
                        commandBuffer.add(0, stringBuffer.toString());
                        terminal.setCursorPosition(0, ++line); // ++ so it advances on first return.
                        terminal.flush();
                        currentBufferPosition = 0;
                        stringBuffer = new StringBuffer();
                        break;
                    case ArrowUp:
                        terminal.setForegroundColor(TextColor.ANSI.MAGENTA);
                        println(terminal, 0, line, commandBuffer.get(currentBufferPosition));
                        currentBufferPosition = Math.min(currentBufferPosition + 1, commandBuffer.size() - 1);
                        System.out.println("Buff pos = " + currentBufferPosition);
                        terminal.flush();
                        break;
                    case ArrowDown:

                        terminal.setForegroundColor(TextColor.ANSI.YELLOW);
                        currentBufferPosition = Math.max(currentBufferPosition - 1, 0);
                        println(terminal, 0, line, commandBuffer.get(currentBufferPosition));
                        System.out.println("Buff pos = " + currentBufferPosition);

                        terminal.flush();
                        break;
                    case Character:
                        currentBufferPosition = 0;

                        if (keyStroke.isAltDown()) {
                            System.out.println("got alt  " + keyStroke.getCharacter());
                            break;
                        }
                        if (keyStroke.isCtrlDown()) {
                            System.out.println("got crtl  " + keyStroke.getCharacter());
                            break;
                        }

                        stringBuffer.append(keyStroke.getCharacter());
                        terminal.putCharacter(keyStroke.getCharacter());
                        terminal.flush();
                        break;
                    case ArrowLeft:
                        currentBufferPosition = 0;

                        terminal.setCursorPosition(terminal.getCursorPosition().getColumn() - 1, terminal.getCursorPosition().getRow());
                        terminal.flush();
                        break;
                    case ArrowRight:
                        // Move cursor right, don't overrun end of line.
                        currentBufferPosition = 0;
                        terminal.setCursorPosition(
                                Math.min(stringBuffer.length() - 1, terminal.getCursorPosition().getColumn() + 1),
                                terminal.getCursorPosition().getRow());
                        terminal.flush();
                        break;
                    case Backspace:
                        if (stringBuffer != null && 0 < stringBuffer.length()) {
                            stringBuffer = stringBuffer.deleteCharAt(stringBuffer.length() - 1);
                            terminal.setCursorPosition(terminal.getCursorPosition().getColumn() - 1, terminal.getCursorPosition().getRow());
                            terminal.putCharacter(' '); // blank what was there
                            terminal.setCursorPosition(terminal.getCursorPosition().getColumn() - 1, terminal.getCursorPosition().getRow());
                            terminal.flush();
                        }
                        break;
                    default:

                }
            }

        }
    }

    public static void println(Terminal t, int col, int row, String x) throws IOException {
        t.setCursorPosition(col, row);
        for (char c : x.toCharArray()) {
            t.putCharacter(c);
        }
    }
}
