var fd
   (File(.new (prepend-home 'sandpit/atmosphere.lsp')))
      .open ^read
 
var parser (ParenParser(.new fd))

write
  parser (.read)
