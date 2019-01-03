# coding=utf-8
import plotly
import plotly.plotly as py
import plotly.graph_objs as go

import numpy as np
import pandas as ps
import sys

GREEDY_COLOR = dict(color='rgb(255,127,14)')
REVERSE_GREEDY_COLOR = dict(color='rgb(31,119,180)')


def get_scatters(results, avgs, name, column_name, color):
    measurements = go.Scatter(
        x=results['n_problem'],
        y=results[column_name],
        mode='markers',
        name=name,
        line=color
    )

    avg = go.Scatter(
        x=avgs.keys(),
        y=avgs,
        mode='lines',
        name=name + ' - povprečje',
        line=color
    )
    return [measurements, avg]


def make_chart(name, greedy_column_index, reverse_gredy_column_index, unit=""):
    results_df = ps.DataFrame(results[:, (0, greedy_column_index, reverse_gredy_column_index)],
                              columns=['n_problem', 'greedy', 'reverse_greedy'])
    avgs = results_df.groupby(['n_problem']).apply(
        lambda x: (np.average(x['greedy']), np.average(x['reverse_greedy']))).apply(ps.Series)

    greedy_scatters = get_scatters(
        results_df,
        avgs[0],
        "Požrešni algoritem",
        'greedy',
        GREEDY_COLOR
    )

    reverse_greedy_scatters = get_scatters(
        results_df,
        avgs[1],
        "Obratni pozresni algoritem",
        'reverse_greedy',
        REVERSE_GREEDY_COLOR
    )
    data = greedy_scatters + reverse_greedy_scatters

    layout = go.Layout(
        title=name,
        xaxis=dict(
            title='Število vozlišč v grafu',
            titlefont=dict(
                family='Courier New, monospace',
                size=18,
                color='#7f7f7f'
            )
        ),
        yaxis=dict(
            title=name if unit == "" else "{0} [{1}]".format(name, unit),
            titlefont=dict(
                family='Courier New, monospace',
                size=18,
                color='#7f7f7f'
            )
        ),
    )

    figure = go.Figure(data=data, layout=layout)
    py.plot(figure, filename=name)


try:
    _, file_name, problem_name = sys.argv
except ValueError as e:
    print("Unpack: ", sys.argv, file=sys.stderr)
    raise
results = np.loadtxt(file_name, delimiter=";")
results[:, 2] = results[:, 2] / 1000000
results[:, 4] = results[:, 4] / 1000000

plotly.tools.set_credentials_file(username='gregor.azbe', api_key='ynSyJDs4GCTyGD4oBkAO')
make_chart(problem_name + " - velikost rešitve", 1, 3)
make_chart(problem_name + " - čas računanja", 2, 4, "ms")
