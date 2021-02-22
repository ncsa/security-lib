package edu.uiuc.ncsa;

/*
This demo won't work off source directory. Look in

~/temp/jline/jline3-jline-parent-3.19.0

issue

./build rebuild

then
cd demo

mvn compile exec:java -Dexec.mainClass="org.jline.demo.Repl"

This starts and runs the demo groovy REPL. The issue is that there are a bunch of demo configuration files
(such as for nano) that are used and this can't operate without them.



 */
/*
 * Copyright (c) 2002-2020, the original author or authors.
 *
 * This software is distributable under the BSD license. See the terms of the
 * BSD license in the documentation provided with this software.
 *
 * https://opensource.org/licenses/BSD-3-Clause
 */

import org.jline.builtins.Completers.OptionCompleter;
import org.jline.builtins.ConfigurationPath;
import org.jline.builtins.Nano.SyntaxHighlighter;
import org.jline.builtins.Options;
import org.jline.console.*;
import org.jline.console.impl.*;
import org.jline.demo.Repl;
import org.jline.keymap.KeyMap;
import org.jline.reader.*;
import org.jline.reader.LineReader.Option;
import org.jline.reader.impl.DefaultParser;
import org.jline.reader.impl.DefaultParser.Bracket;
import org.jline.reader.impl.LineReaderImpl;
import org.jline.reader.impl.completer.ArgumentCompleter;
import org.jline.reader.impl.completer.NullCompleter;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.script.GroovyCommand;
import org.jline.script.GroovyEngine;
import org.jline.terminal.Size;
import org.jline.terminal.Terminal;
import org.jline.terminal.Terminal.Signal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.InfoCmp;
import org.jline.utils.InfoCmp.Capability;
import org.jline.utils.OSUtils;
import org.jline.widget.TailTipWidgets;
import org.jline.widget.TailTipWidgets.TipType;
import org.jline.widget.Widgets;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Demo how to create REPL app with JLine.
 *
 * @author <a href="mailto:matti.rintanikkola@gmail.com">Matti Rinta-Nikkola</a>
 */
public class MyREPL {

    protected static class MyCommands extends JlineCommandRegistry implements CommandRegistry {
        private LineReader reader;
        private final Supplier<Path> workDir;

        public MyCommands(Supplier<Path> workDir) {
            super();
            this.workDir = workDir;
            Map<String,CommandMethods> commandExecute = new HashMap<>();
            commandExecute.put("tput", new CommandMethods(this::tput, this::tputCompleter));
            commandExecute.put("testkey", new CommandMethods(this::testkey, this::defaultCompleter));
            commandExecute.put("clear", new CommandMethods(this::clear, this::defaultCompleter));
            commandExecute.put("!", new CommandMethods(this::shell, this::defaultCompleter));
            registerCommands(commandExecute);
        }

        public void setLineReader(LineReader reader) {
            this.reader = reader;
        }

        private Terminal terminal() {
            return reader.getTerminal();
        }

        private void tput(CommandInput input) {
            final String[] usage = {
                    "tput -  put terminal capability",
                    "Usage: tput [CAPABILITY]",
                    "  -? --help                       Displays command help"
            };
            try {
                Options opt = parseOptions(usage, input.xargs());
                List<String> argv = opt.args();
                if (argv.size() > 0) {
                    Capability vcap = Capability.byName(argv.get(0));
                    if (vcap != null) {
                        terminal().puts(vcap, opt.argObjects().subList(1, argv.size()).toArray(new Object[0]));
                    } else {
                        terminal().writer().println("Unknown capability");
                    }
                } else {
                    terminal().writer().println("Usage: tput [CAPABILITY]");
                }
            } catch (Exception e) {
                saveException(e);
            }
        }

        private void testkey(CommandInput input) {
            final String[] usage = {
                    "testkey -  display the key events",
                    "Usage: testkey",
                    "  -? --help                       Displays command help"
            };
            try {
                parseOptions(usage, input.args());
                terminal().writer().write("Input the key event(Enter to complete): ");
                terminal().writer().flush();
                StringBuilder sb = new StringBuilder();
                while (true) {
                    int c = ((LineReaderImpl) reader).readCharacter();
                    if (c == 10 || c == 13) break;
                    sb.append(new String(Character.toChars(c)));
                }
                terminal().writer().println(KeyMap.display(sb.toString()));
                terminal().writer().flush();
            } catch (Exception e) {
                saveException(e);
            }
        }

        private void clear(CommandInput input) {
            final String[] usage = {
                    "clear -  clear terminal",
                    "Usage: clear",
                    "  -? --help                       Displays command help"
            };
            try {
                parseOptions(usage, input.args());
                terminal().puts(Capability.clear_screen);
                terminal().flush();
            } catch (Exception e) {
                saveException(e);
            }
        }

        private void executeCmnd(List<String> args) throws Exception {
            ProcessBuilder builder = new ProcessBuilder();
            List<String> _args = new ArrayList<>();
            if (OSUtils.IS_WINDOWS) {
                _args.add("cmd.exe");
                _args.add("/c");
            } else {
                _args.add("sh");
                _args.add("-c");
            }
            _args.add(String.join(" ", args));
            builder.command(_args);
            builder.directory(workDir.get().toFile());
            Process process = builder.start();
            StreamGobbler streamGobbler = new StreamGobbler(process.getInputStream(), System.out::println);
            Thread th = new Thread(streamGobbler);
            th.start();
            int exitCode = process.waitFor();
            th.join();
            if (exitCode != 0) {
                throw new Exception("Failed to execute: " + String.join(" ", args.subList(2, args.size())));
            }
        }

        private void shell(CommandInput input) {
            final String[] usage = { "!<command> -  execute shell command"
                                   , "Usage: !<command>"
                                   , "  -? --help                       Displays command help" };
            if (input.args().length == 1 && (input.args()[0].equals("-?") || input.args()[0].equals("--help"))) {
                try {
                    parseOptions(usage, input.args());
                } catch (Exception e) {
                    saveException(e);
                }
            } else {
                List<String> argv = new ArrayList<>(Arrays.asList(input.args()));
                if (!argv.isEmpty()) {
                    try {
                        executeCmnd(argv);
                    } catch (Exception e) {
                        saveException(e);
                    }
                }
            }
        }

        private Set<String> capabilities() {
            return InfoCmp.getCapabilitiesByName().keySet();
        }

        private List<Completer> tputCompleter(String command) {
            List<Completer> completers = new ArrayList<>();
            completers.add(new ArgumentCompleter(NullCompleter.INSTANCE
                                               , new OptionCompleter(new StringsCompleter(this::capabilities)
                                                                   , this::commandOptions
                                                                   , 1)
                                                ));
            return completers;
        }

    }

    private static class StreamGobbler implements Runnable {
        private final InputStream inputStream;
        private final Consumer<String> consumer;

        public StreamGobbler(InputStream inputStream, Consumer<String> consumer) {
            this.inputStream = inputStream;
            this.consumer = consumer;
        }

        @Override
        public void run() {
            new BufferedReader(new InputStreamReader(inputStream)).lines()
              .forEach(consumer);
        }
    }

    private static Path workDir() {
        return Paths.get(System.getProperty("user.dir"));
    }

    public static void main(String[] args) {
        try {
            //
            // Parser & Terminal
            //
            DefaultParser parser = new DefaultParser();
            parser.setEofOnUnclosedBracket(Bracket.CURLY, Bracket.ROUND, Bracket.SQUARE);
            parser.setEofOnUnclosedQuote(true);
            parser.setEscapeChars(null);
            parser.setRegexCommand("[:]{0,1}[a-zA-Z!]{1,}\\S*");    // change default regex to support shell commands
            Terminal terminal = TerminalBuilder.builder().build();
            if (terminal.getWidth() == 0 || terminal.getHeight() == 0) {
                terminal.setSize(new Size(120, 40));   // hard coded terminal size when redirecting
            }
            Thread executeThread = Thread.currentThread();
            terminal.handle(Signal.INT, signal -> executeThread.interrupt());
            //
            // Create jnanorc config file for demo
            //
            File file = new File(Repl.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
            String root = file.getCanonicalPath().replace("classes", "").replaceAll("\\\\", "/"); // forward slashes works better also in windows!
            File jnanorcFile = Paths.get(root, "jnanorc").toFile();
            if (!jnanorcFile.exists()) {
                jnanorcFile = new File("/tmp/nanorc");
                FileWriter fw = new FileWriter(jnanorcFile);
                fw.write("include " + root + "nanorc/*.nanorc\n");
                fw.close();
            }
            //
            // ScriptEngine and command registries
            //
            GroovyEngine scriptEngine = new GroovyEngine();
            scriptEngine.put("ROOT", root);
            ConfigurationPath configPath = new ConfigurationPath(Paths.get(root), Paths.get(root));
            Printer printer = new DefaultPrinter(scriptEngine, configPath);
            ConsoleEngineImpl consoleEngine = new ConsoleEngineImpl(scriptEngine
                                                                  , printer
                                                                  , MyREPL::workDir, configPath);
            Builtins builtins = new Builtins(MyREPL::workDir, configPath,  (String fun)-> new ConsoleEngine.WidgetCreator(consoleEngine, fun));
            MyCommands myCommands = new MyCommands(MyREPL::workDir);
            SystemRegistryImpl systemRegistry = new SystemRegistryImpl(parser, terminal, MyREPL::workDir, configPath);
            systemRegistry.register("groovy", new GroovyCommand(scriptEngine, printer));
            systemRegistry.setCommandRegistries(consoleEngine, builtins, myCommands);
            systemRegistry.addCompleter(scriptEngine.getScriptCompleter());
            systemRegistry.setScriptDescription(scriptEngine::scriptDescription);
            //
            // LineReader
            //
            Path jnanorc = configPath.getConfig("jnanorc");
            SyntaxHighlighter commandHighlighter = SyntaxHighlighter.build(jnanorc,"COMMAND");
            SyntaxHighlighter argsHighlighter = SyntaxHighlighter.build(jnanorc,"ARGS");
            SyntaxHighlighter groovyHighlighter = SyntaxHighlighter.build(jnanorc,"Groovy");
            LineReader reader = LineReaderBuilder.builder()
                    .terminal(terminal)
                    .completer(systemRegistry.completer())
                    .parser(parser)
                    .highlighter(new SystemHighlighter(commandHighlighter, argsHighlighter, groovyHighlighter))
                    .variable(LineReader.SECONDARY_PROMPT_PATTERN, "%M%P > ")
                    .variable(LineReader.INDENTATION, 2)
                    .variable(LineReader.LIST_MAX, 100)
                    .variable(LineReader.HISTORY_FILE, Paths.get(root, "history"))
                    .option(Option.INSERT_BRACKET, true)
                    .option(Option.EMPTY_WORD_OPTIONS, false)
                    .option(Option.USE_FORWARD_SLASH, true)             // use forward slash in directory separator
                    .option(Option.DISABLE_EVENT_EXPANSION, true)
                    .build();
            if (OSUtils.IS_WINDOWS) {
                reader.setVariable(LineReader.BLINK_MATCHING_PAREN, 0); // if enabled cursor remains in begin parenthesis (gitbash)
            }
            //
            // complete command registries
            //
            consoleEngine.setLineReader(reader);
            builtins.setLineReader(reader);
            myCommands.setLineReader(reader);
            //
            // widgets and console initialization
            //
            new TailTipWidgets(reader, systemRegistry::commandDescription, 5, TipType.COMPLETER);
            KeyMap<Binding> keyMap = reader.getKeyMaps().get("main");
            keyMap.bind(new Reference(Widgets.TAILTIP_TOGGLE), KeyMap.alt("s"));
            systemRegistry.initialize(Paths.get(root, "init.jline").toFile());
            //
            // REPL-loop
            //
            System.out.println(terminal.getName() + ": " + terminal.getType());
            while (true) {
                try {
                    systemRegistry.cleanUp();         // delete temporary variables and reset output streams
                    String line = reader.readLine("groovy-repl> ");
                    line = parser.getCommand(line).startsWith("!") ? line.replaceFirst("!", "! ") : line;
                    Object result = systemRegistry.execute(line);
                    consoleEngine.println(result);
                }
                catch (UserInterruptException e) {
                    // Ignore
                }
                catch (EndOfFileException e) {
                    String pl = e.getPartialLine();
                    if (pl != null) {                 // execute last line from redirected file (required for Windows)
                        try {
                            consoleEngine.println(systemRegistry.execute(pl));
                        } catch (Exception e2) {
                            systemRegistry.trace(e2);
                        }
                    }
                    break;
                }
                catch (Exception e) {
                    systemRegistry.trace(e);          // print exception and save it to console variable
                }
            }
            systemRegistry.close();                   // persist pipeline completer names etc

            Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
            boolean groovyRunning=false;              // check Groovy GUI apps
            for (Thread t : threadSet) {
                if (t.getName().startsWith("AWT-Shut")) {
                    groovyRunning = true;
                    break;
                }
            }
            if (groovyRunning) {
                consoleEngine.println("Please, close Groovy Consoles/Object Browsers!");
            }
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
