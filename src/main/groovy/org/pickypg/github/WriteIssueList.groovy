/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.pickypg.github

import org.kohsuke.github.GHIssue
import org.kohsuke.github.GHIssueSearchBuilder
import org.kohsuke.github.GHOrganization
import org.kohsuke.github.GHRepository
import org.kohsuke.github.GitHub
import org.kohsuke.github.PagedSearchIterable

import java.util.regex.Matcher

/**
 * {@code WriteIssueList} will write all issues that match the command line arguments. Each issue is written as a
 * separate text file in the working directory using the GitHub issue number as the name of the file (issue_number.txt).
 * <p>
 * For example, imagine that this project is compiled as "github-issue-exporter.jar", then you could run it as
 * <pre>
 * java -jar github-issue-exporter.jar "repo:elastic/elasticsearch" "is:open" "label:v2.0.0" "label:v1.5.0" "type:issue" "is:open"
 * </pre>
 * This would search for issues that:
 * <ul>
 * <li>Are open (as opposed to closed).</li>
 * <li>Are labeled with two labels: "v2.0.0" and "v1.5.0"</li>
 * <li>Are <em>issues</em> (as opposed to pull requests, which you would use "is:pr" to search)</li>
 * <li>Are contained in the elastic/elasticsearch repository (as opposed to all repositories or a fork).</li>
 * </ul>
 * Order of arguments is irrelevant.
 */
class WriteIssueList {

    /**
     * Entry point to fetch all {@link GHIssue GitHub issues} that match the passed in {@code args}.
     *
     * @param args The arguments that are each used as search terms for {@link GHIssue GitHub issues}.
     */
    static void main(String[] args) {
        // Connect to GitHub using the credentials stored at "~/.github" as a properties file containing:
        //
        //   login=YOUR_USERNAME
        //   password=YOUR_PASSWORD
        //
        GitHub github = GitHub.connect();

        // prepare to search for open issues
        GHIssueSearchBuilder searchBuilder = github.searchIssues()

        // search for issues (and pull requests) based on the passed in criteria
        //  For example:
        //  java -jar this.jar "is:open" "label:design"
        args.each {
            searchBuilder.q(it)
        }

        // get the repository
        PagedSearchIterable<GHIssue> issues = searchBuilder.list()

        // list each issue
        issues.each { issue ->
            new File(issue.number + '.txt').withWriter('utf-8') { writer ->
                // write details about the issue
                writer.writeLine "Issue # ${issue.number} (${issue.htmlUrl})"
                writer.writeLine "Reporter: ${issue.user.login} (${issue.user.name})"
                writer.writeLine "Posted: ${issue.createdAt.format('yyyy-MM-dd HH:mm:ss')}"
                writer.writeLine "Title: ${issue.title}"
                writer.writeLine 'Body:'
                writer.writeLine ''
                writer.writeLine issue.body
                writer.writeLine ''
                writer.writeLine '---------------------'
                writer.writeLine "Comments (${issue.commentsCount})"

                // page the comments if we have any
                if (issue.commentsCount != 0) {
                    GHIssue commentedIssue = issue

                    // for some reason the API is returning a null repo, even though it can clearly figure it out
                    if (issue.repository == null) {
                        // find the owner/repo from the actual issue
                        Matcher matcher = issue.htmlUrl =~ /.*?github.com\\/(.*)\\/(.*)\\/issues.*/

                        // we were successfully able to parse the owner/repo
                        if (matcher.matches()) {
                            GHOrganization organization = github.getOrganization(matcher.group(1))
                            GHRepository repository = organization.getRepository(matcher.group(2))

                            // replace the issue
                            commentedIssue = repository.getIssue(issue.number)
                        }
                        // failed to parse
                        else {
                            // skip the comments
                            commentedIssue = null
                        }
                    }

                    // write out comments
                    if (commentedIssue != null) {
                        // write out each comment
                        commentedIssue.listComments().each {
                            writer.writeLine '---------------------'
                            writer.writeLine ''
                            writer.writeLine "Comment #${it.id} (${issue.htmlUrl}#issuecomment-${it.id})"
                            writer.writeLine "Commenter: ${it.user.login} (${it.user.name})"
                            writer.writeLine "Posted: ${it.createdAt.format('yyyy-MM-dd HH:mm:ss')}"
                            writer.writeLine 'Body:'
                            writer.writeLine ''
                            writer.writeLine it.body
                            writer.writeLine ''
                        }
                    }
                    // acknowledge why there are no comments being displayed
                    else {
                        writer.writeLine '---------------------'
                        writer.writeLine ''
                        writer.writeLine 'Error Reading Comments'
                    }
                }
            }
        }
    }
}