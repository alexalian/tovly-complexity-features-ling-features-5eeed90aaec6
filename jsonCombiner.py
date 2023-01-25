import json

newsela = json.load(open('Newsela__None.json', 'r'))
weebit = json.load(open('WeeBit__None.json', 'r'))
combined = newsela + weebit
json.dump(combined, open('combined.json', 'w'))