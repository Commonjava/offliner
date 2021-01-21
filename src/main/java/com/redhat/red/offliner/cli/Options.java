/*
 * Copyright (C) 2015 Red Hat, Inc. (jcasey@redhat.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.redhat.red.offliner.cli;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.ParserProperties;

import java.io.File;
import java.util.List;

/**
 * Command-line option specification, along with utility methods to parse arguments and print usage.
 */
public class Options
{

    private static final int DEFAULT_CONNECTIONS = 500;

    public static final String DEFAULT_REPO_URL = "https://maven.repository.redhat.com/ga/all/";

    public static final String CENTRAL_REPO_URL = "https://repo.maven.apache.org/maven2/";

    public static final String ERROR_LOG = "errors.log";

    public static final String HEADER_START = "#header";

    public static final String HEADER_BREAK_REGEX = "---.+";

    private static final File DEFAULT_DOWNLOADS = new File( "repository" );

    private static final Integer DEFAULT_THREADS = 4 * Runtime.getRuntime().availableProcessors();

    @Option( name = "-M", aliases = { "--no-metadata" },
             usage = "Do NOT generate maven-metadata.xml files for downloaded content" )
    private boolean skipMetadata;

    @Option( name = "-r", aliases = { "--url", "--repo-url", "--base-url" }, metaVar = "REPO-URL",
             usage = "Alternative URL for resolving repository artifacts (eg. repository manager URL for proxy of maven.repository.redhat.com)" )
    private List<String> baseUrls;

    @Option( name = "-u", aliases = { "--user", "--repo-user" }, metaVar = "USER",
             usage = "Authentication user, if using a repository manager URL" )
    private String user;

    @Option( name = "-p", aliases = { "--password", "--repo-pass" }, metaVar = "PASS",
             usage = "Authentication password, if using a repository manager URL" )
    private String password;

    @Option( name = "-x", aliases = { "--proxy" }, metaVar = "HOST[:PORT]",
             usage = "Proxy host and port (optional) to use for downloads" )
    private String proxy;

    @Option( name = "-U", aliases = { "--proxy-user" }, metaVar = "USER", usage = "User for authenticating to a proxy" )
    private String proxyUser;

    @Option( name = "-P", aliases = { "--proxy-pass" }, metaVar = "PASS",
             usage = "Password for authenticating to a proxy" )
    private String proxyPassword;

    @Option( name = "-c", aliases = { "--connections" }, metaVar = "INT",
             usage = "Number of concurrent connections to allow for downloads (default: 200)" )
    private Integer connections;

    @Option( name = "-d", aliases = { "--download", "--dir" }, metaVar = "DIR",
             usage = "Download directory (default: ./repository)" )
    private File downloads;

    @Option( name = "-s", aliases = { "--mavensettings" }, metaVar = "FILE",
             usage = "Path to settings.xml used when a pom is used as the source file" )
    private File settingsXml;

    @Option( name = "-m", aliases = { "--maventypemapping" }, metaVar = "MAPPING",
             usage = "File containing mapping properties "
                     + "where key is type and value is file extension with or without classifier each mapping on a single line. List elements"
                     + " are separated by semicolons." )
    private String typeMapping;

    @Option( name = "-T", aliases = { "--threads" }, metaVar = "INT",
             usage = "Number of concurrent threads to allow for downloads (default: 4xCPU)" )
    private Integer threads;

    @Option( name = "-h", aliases = { "--help" }, help = true, usage = "Print this help screen and exit" )
    private boolean help;

    @Argument( multiValued = true, metaVar = "FILES", usage = "List of files containing artifact paths to download. " +
            "At the header of the manifest(text-listing) file, option and its value could be declared which normally specify on CLI. " +
            "Header options and values should format as ini-style, option alias should be used as the param name per line. " +
            "Note, the options in CLI arguments line will cover the header ones.\n" +
            "Example:\n" +
            "#header\n"+
            "no-metadata (no need to specify its boolean value)\n" +
            "download=./repo\n" +
            "----\n" )
    private List<String> locations;

    public boolean parseArgs( final String[] args )
            throws CmdLineException
    {
        boolean canStart = true;
        CmdLineParser parser = doParse( args );

        if ( isHelp() )
        {
            printUsage( parser, null );
            canStart = false;
        }

        return canStart;
    }

    public CmdLineParser doParse( final String[] args )
            throws CmdLineException
    {
        final int cols = ( System.getenv( "COLUMNS" ) == null ? 100 : Integer.valueOf( System.getenv( "COLUMNS" ) ) );
        final ParserProperties props = ParserProperties.defaults().withUsageWidth( cols );

        final CmdLineParser parser = new CmdLineParser( this, props );
        parser.parseArgument( args );
        return parser;
    }

    public static void printUsage( final CmdLineParser parser, final CmdLineException error )
    {
        if ( error != null )
        {
            System.err.println( "Invalid option(s): " + error.getMessage() );
            System.err.println();
        }

        System.err.println( "Usage: $0 [OPTIONS] FILES" );
        System.err.println();
        System.err.println();
        parser.printUsage( System.err );
        System.err.println();
    }

    public List<String> getBaseUrls()
    {
        return baseUrls;
    }

    public void setBaseUrls( final List<String> baseUrls )
    {
        this.baseUrls = baseUrls;
    }

    public boolean isHelp()
    {
        return help;
    }

    public void setHelp( final boolean help )
    {
        this.help = help;
    }

    public List<String> getLocations()
    {
        return locations;
    }

    public void setLocations( final List<String> locations )
    {
        this.locations = locations;
    }

    public String getUser()
    {
        return user;
    }

    public void setUser( final String user )
    {
        this.user = user;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword( final String password )
    {
        this.password = password;
    }

    public String getProxy()
    {
        return proxy;
    }

    public void setProxy( final String proxy )
    {
        this.proxy = proxy;
    }

    public String getProxyUser()
    {
        return proxyUser;
    }

    public void setProxyUser( final String proxyUser )
    {
        this.proxyUser = proxyUser;
    }

    public String getProxyPassword()
    {
        return proxyPassword;
    }

    public void setProxyPassword( final String proxyPassword )
    {
        this.proxyPassword = proxyPassword;
    }

    public Integer getConnections()
    {
        return connections == null ? DEFAULT_CONNECTIONS : connections;
    }

    public void setConnections( final Integer connections )
    {
        this.connections = connections;
    }

    public File getDownloads()
    {
        return downloads == null ? DEFAULT_DOWNLOADS : downloads;
    }

    public void setDownloads( final File downloads )
    {
        this.downloads = downloads;
    }

    public File getSettingsXml()
    {
        return settingsXml;
    }

    public void setSettingsXml( final File settingsXml )
    {
        this.settingsXml = settingsXml;
    }

    public String getTypeMapping()
    {
        return typeMapping;
    }

    public void setTypeMapping( final String typeMapping )
    {
        this.typeMapping = typeMapping;
    }

    public Integer getThreads()
    {
        return threads == null ? DEFAULT_THREADS : threads;
    }

    public void setThreads( final Integer threads )
    {
        this.threads = threads;
    }

    public boolean isSkipMetadata()
    {
        return skipMetadata;
    }

    public void setSkipMetadata( final boolean skipMetadata )
    {
        this.skipMetadata = skipMetadata;
    }
}
