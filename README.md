# Advent of Code 2024

I'm solving the [Advent of Code](https://adventofcode.com/) as a way of learning new programming languages. This year I'm doing it in [Clojure](https://www.clojure.org/) to learn another lispy language!

## Lessons learned

- 01: `(frequencies some-list)` is an amazing builtin. Also I miss Scheme's playful `(string->int)` style parsing functions. `(Integer/parseInt)` feels too professional.
- 02: You can do keyword arguments with map-destructuring, but they are not checked for validity? As in, if my keyword is `:damped?` but someone passes `:damped`, it just silently fails.
- 03: Regex primitives cannot be manipulated (I guess they are immediately compiled?). Also `re-matches` only hits a single match, whereas `re-seq` gets the lot.
- 04: I'm kind of surprised there is no standard library `rotate` function for rotating the elements of a sequence. And `get` returning `nil` if the index is out of bounds is kind of convenient for implicitly padding a matrix, but it feels like a footgun.
- 05: Absolutely dislike that `(contains?)` assumes every collection is **map** and so is only checking every 2nd value. I do like every dict is a default dict because you can `(get coll index '[])`! There must be a better way to insert a vector into a sorted set? Probably `into` can do it. I think I reach for `loop` too often, where `reduce` might be better?
- 06: Vector operations are actually really easy with `(apply + v1 v2)`. And being able to abuse what the return types are is also kind of fun, but it feels incredibly dangerous. And recursion was actually really helpful for this one, although my solution is slow...
- 07: It seems you can append `'` to an operator to make it do type promotion automatically; for instance, `*'` will automatically expand to `bigint` if needed. Also, brute force is definitely not the answer.
- 08: Vector manipulations are easiest when abstracted into small functions. I made too many mistakes trying to write the `(mapv (apply))` in-place.
- 09: This day seemed unusually tricky in Clojure. I think I could have made my first part much more straight forward with using `assoc` to effectively "mutate" the vector. Also learned that `concat` is lazy, and can easily lead to overflowed stacks, so better to use `into` unless there's a reason to defer evaluation.

