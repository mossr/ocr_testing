tesseract rcp.training_receipt.jpg rcp.training_receipt batch.nochop makebox
tesseract rcp.training_receipt.jpg rcp.training_receipt.box  nobatch box.train
unicharset_extractor rcp.training_receipt.box
echo training_receipt 0 0 0 0 0 > font_properties
mftraining -F font_properties -U unicharset -O rcp.unicharset rcp.training_receipt.box.tr
cntraining rcp.training_receipt.box.tr
forfiles /S /M * /C "cmd /c rename @file rcp.@fname"
combine_tessdata rcp.