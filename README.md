# github-issue-exporter

Simple Groovy-based Issue Exporter for GitHub

# Requirements

- GitHub account
- Java 7+ Installed

# Setup

Create a file in your home directory `.github` that looks like:

```properties
login=YOUR_USERNAME
password=YOUR_PASSWORD
```

# Running

In order to run it, you need to build from source, which is done automatically for you by running the commands below.
The first command compiles the code and places the compiled files in the `build` directory. No files are ever written
outside of the current directory.

After building it, you simply need to run it.

You need to replace `"repo:OWNER/REPO"` with the repository details that you intend to search. For example, if you
wanted to search [elastic](https://github.com/elastic)'s [elasticsearch](https://github.com/elastic/elasticsearch)
repository, then you would change this to `"repo:elastic/elasticsearch"`.

## Linux / Mac OS X

```
$ ./gradlew installApp
$ build/install/github-issue-exporter/bin/github-issue-exporter "repo:OWNER/REPO" "type:issue" "is:open"
```

## Windows

```
> gradlew installApp
> build\install\github-issue-exporter\bin\github-issue-exporter "repo:OWNER/REPO" "type:issue" "is:open"
```

Once you have run it, you can check this folder for `*.txt` files. There will be one per issue found.

## Searching for issues

Each argument passed to `github-issue-exporter` (the quoted parts following it above) [follows the syntax set by
GitHub](https://developer.github.com/v3/search/#search-issues). For example, `"label:your-label"` would limit the
search to issues that are labeled with `your-label`. You could further limit the search by adding another label, such
as `"label:your-label" "label:your-other-label"`.

## Searching for pull requests

You can also use this tool to search for pull requests by changing `"type:issue"` to `"type:pr"`. You can search for
both open and closed issues by removing the search term entirely.

## Searching for closed issues

You can search for closed issues by changing `"is:open"` to `"is:closed"`. You can search for both open and closed
issues by removing the search term entirely.

License
-------

    This software is licensed under the Apache 2 license, quoted below.

    Copyright 2009-2014 Elasticsearch <http://www.elasticsearch.org>

    Licensed under the Apache License, Version 2.0 (the "License"); you may not
    use this file except in compliance with the License. You may obtain a copy of
    the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
    WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
    License for the specific language governing permissions and limitations under
    the License.