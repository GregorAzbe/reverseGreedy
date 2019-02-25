# coding=utf-8
import plotly
import plotly.plotly as py
import plotly.graph_objs as go

import numpy as np
import pandas as ps
import sys

GREEDY_COLOR = dict(color='rgb(255,127,14)')
REVERSE_GREEDY_COLOR = dict(color='rgb(31,119,180)')
FILE_NAME = "../Barvanje grafov - final.csv"
PROBLEM_NAME = "Barvanje grafov"
X_AXIS_LABEL = "Število povezav v grafu"
FINAL = False


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


def make_chart(name, results, x_axis_label, y_axis_label, greedy_column_index, reverse_gredy_column_index, use_median, unit=""):
    results_df = ps.DataFrame(results[:, (0, greedy_column_index, reverse_gredy_column_index)],
                              columns=['n_problem', 'greedy', 'reverse_greedy'])

    avgs_or_medians = results_df.groupby(['n_problem']).apply(
        lambda x: (np.median(x['greedy']) if use_median else np.average(x['greedy']), np.median(x['reverse_greedy']) if use_median else np.average(x['reverse_greedy']))
    ).apply(ps.Series)

    greedy_scatters = get_scatters(
        results_df,
        avgs_or_medians[0],
        "Požrešni algoritem",
        'greedy',
        GREEDY_COLOR
    )

    reverse_greedy_scatters = get_scatters(
        results_df,
        avgs_or_medians[1],
        "Izločevalni algoritem",
        'reverse_greedy',
        REVERSE_GREEDY_COLOR
    )
    data = greedy_scatters + reverse_greedy_scatters

    layout = go.Layout(
        title=name,
        xaxis=dict(
            title=x_axis_label,
            titlefont=dict(
                family='Courier New, monospace',
                size=18,
                color='#7f7f7f'
            )
        ),
        yaxis=dict(
            title=y_axis_label if unit == "" else "{0} [{1}]".format(y_axis_label, unit),
            titlefont=dict(
                family='Courier New, monospace',
                size=18,
                color='#7f7f7f'
            )
        ),
    )

    figure = go.Figure(data=data, layout=layout)
    py.plot(figure, filename=name)

def make_charts(file_name, problem_name, x_axis_label):
    results = np.loadtxt(file_name, delimiter=";")
    results[:, 2] = results[:, 2] / 1000000
    results[:, 4] = results[:, 4] / 1000000

    plotly.tools.set_credentials_file(username='gregor.azbe', api_key='ynSyJDs4GCTyGD4oBkAO')
    make_chart(problem_name + " - primerjava uspešnosti algoritmov", results, x_axis_label, "Velikost rešitve", 1, 3, False)
    make_chart(problem_name + "  - primerjava učinkovitosti algoritmov", results, x_axis_label, "Čas računanja", 2, 4, True, "ms")

if len(sys.argv) > 1:
    try:
        _, file_name, problem_name, x_axis_label = sys.argv
    except ValueError as e:
        print("Unpack: ", sys.argv, file=sys.stderr)
        raise
    make_charts(file_name, problem_name, x_axis_label)

else:
    if FINAL:
        make_charts("../Dominating set - final.csv", "Dominantna množica", "Število vozlišč v grafu")
        make_charts("../Barvanje grafov - final.csv", "Barvanje grafov", "Število povezav v grafu")
        make_charts("../Vertex cover - final.csv", "Vozliščno pokritje", "Število vozlišč v grafu")

    else:
        file_name = FILE_NAME
        problem_name = PROBLEM_NAME
        x_axis_label = X_AXIS_LABEL
        make_charts(file_name, problem_name, x_axis_label)
