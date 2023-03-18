# Computing sets FIRST and FOLLOW
## Input specification
The input contains a context free grammar definition. Non-terminals are upper-case letters, terminals are lower-case letters. It composes from several lines; each line ends with the semicolon, and it contains rules for one nonterminal. The line starts with this nonterminal, then there is a colon and it is followed by right hand sides of its rules separated by `|`.

There may be also notes in the input, they are written in compose brackets. They have no meaning for the grammar definition.

For more detail see the example.

## Output specification
For the context free grammar from the input write FIRST sets for the right-hand side of all its rules. Next write computed FOLLOW sets for all grammar non-terminals.

Output’s formatting is not strict, your output should be like the output from the example.

## Example
* Input
```
{Input grammar}
A : b C | B d;
B : C C | b A;
C : c C | {e};
```

* Output
```
first[A:bC] = b
first[A:Bd] = b c d
first[B:CC] = c {e}
first[B:bA] = b
first[C:cC] = c
first[C:{e}] = {e}
follow[A] = d $
follow[B] = d
follow[C] = c d $
```
