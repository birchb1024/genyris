def prepend-home (relative-path) (System!HOME (.+ '/' relative-path))

spawn (prepend-home "examples/spawn-example.g") 1 ^(2 we) 23.45
include 'test/acceptance/test-synch.g'