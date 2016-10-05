# au-java-II-fall-2016

Primitive VCS(version control system) with no future

Commands and usage:
- `add $FILE1 $FILE2...` - adds files to index
- `branch -c branch_name` - creates empty branch `branch_name` and switches to it. If changes are present, then does not create
- `branch -d branch_name` - removes branch and all commits within it from repository.
- `checkout` - performs checkout. Depending on parameters, can checkout branch on a latest commit, a particular commit or a particular commit in a particular branch. Examples:
    * `checkout -b branch_name` - checkout `branch_name` at latest commit in it
    * `checkout -r commit_num` - checkout to commit `commit_num`. Current branch is detached, so common use-case is create new branch from this point
- `clean` - remove all files that were not added ito repository
- `commit -m commit_message` - commits added files 
- `log` - shows log of of commits with messages in current branch
- `merge src` - merges branch `src` into current branch. Does not allows conflicts
- `reset $FILE` - removes `$FILE` from index
- `rm $FILE1, $FILE2, ...` - removes physically `$FILE1, $FILE2, ...` 
- `status` - shows info about added, removed, modified and untracked files.
    
