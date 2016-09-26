# au-java-II-fall-2016

Primitive VCS(version control system) with no future

Commands and usage:
- `add $FILE1 $FILE2...` - adds files to index
- `branch -c branch_name` - creates empty branch `branch_name` and switches to it. If changes are present, then does not create
- `branch -d branch_name` - removes branch and all commits within it from repository.
- `commit -m commit_message` - commits added files 
- `checkout` - performs checkout. Depending on parameters, can checkout branch on a latest commit, a particular commit or a particular commit in a particular branch. Examples:
    * `checkout -b branch_name` - checkout `branch_name` at latest commit in it
    * `checkout -r commit_num` - checkout branch with commit `commit_num`
    * `checkout -b branch_name - r commit_num` - checkout `branch_name` at commit `commit_num` in it
- `log` - shows log of of commits with messages in current branch
- `merge src dst` - merges branch `src` into `dst`. Does not allows conflicts
    
