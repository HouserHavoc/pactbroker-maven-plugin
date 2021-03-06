package com.github.wrm.pact.maven;

import java.util.Optional;

import org.apache.maven.plugin.AbstractMojo;
import org.codehaus.plexus.util.StringUtils;
import org.eclipse.jgit.transport.CredentialsProvider;

import com.github.wrm.pact.git.auth.BasicGitCredentialsProvider;
import com.github.wrm.pact.repository.BrokerRepositoryProvider;
import com.github.wrm.pact.repository.GitRepositoryProvider;
import com.github.wrm.pact.repository.RepositoryProvider;

public abstract class AbstractPactsMojo extends AbstractMojo {

    /**
     * returns an implementation of RepositorProvider based on given url
     *
     * accepts authentication, if possible (currently, only supported for git repositories
     *
     *
     * @param url
     * @return
     */
    protected RepositoryProvider createRepositoryProvider(String url, String consumerVersion,
                                                          Optional<String> username, Optional<String> password) {
        if (url.endsWith(".git")){
            Optional<CredentialsProvider> credentialProvider = getCredentialsProvider(username, password);
            return new GitRepositoryProvider(url, getLog(), credentialProvider);
        }
        return new BrokerRepositoryProvider(url, consumerVersion, getLog(), username, password);
    }


    private Optional<CredentialsProvider> getCredentialsProvider(Optional<String> username, Optional<String> password) {
        return username
                .filter(StringUtils::isNotEmpty)
                .flatMap(u -> password
                        .filter(StringUtils::isNotEmpty)
                        .map(p -> new BasicGitCredentialsProvider().getCredentialProvider(u,p)));

    }
}
